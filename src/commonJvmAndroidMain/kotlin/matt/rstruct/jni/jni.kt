package matt.rstruct.jni

import matt.lang.anno.SeeURL
import matt.model.code.mod.RelativeToKMod
import matt.model.code.mod.sharedLibBaseName
import matt.rstruct.loader.ClassicResourceLoader
import matt.rstruct.loader.withResourceStream
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import kotlin.io.path.pathString
import kotlin.reflect.KClass


const val NATIVE_RESOURCE_FOLDER_NAME = "native"
fun RelativeToKMod.sharedLibName() =
    "lib$sharedLibBaseName.dylib"/*fun sharedLibResource(mod: RelativeToKMod) = systemResourceLoader().readResourceAsText()*/

private val loadedLibraries = mutableSetOf<String>()

abstract class SystemLoadCaller {
    /*


    implementations should literally copy and paste this exactly code:

     System.load(filename)

    That is it! The whole point of this is because System.load uses reflection to get the caller class, so this helps me control who the caller class is. Therefore, this must be strictly controlled.

    If one day I ever mess this up, I will just have to keep inventing more and more clever mechanisms to enforce this, since we lost all compile time safety with this horrific decision by java.

     */
    abstract fun callSystemLoad(filename: String)
}

@Synchronized
fun markSharedLibraryResourceUnloaded(
    mod: RelativeToKMod
) {
    check(loadedLibraries.remove(mod.sharedLibName()))
}
@SeeURL(
    "https://docs.oracle.com/en/java/javase/20/docs/specs/jni/invocation.html#library-and-version-management"
)
/**
 * Completely SAFE dynamically loaded code. SAFER than every alternative I can imagine. The SAFEST option possible.
 */
@Synchronized
fun loadSharedLibraryResource(
    resourceLoader: ClassicResourceLoader,
    mod: RelativeToKMod,
    classLoaderOfClass: KClass<*>
) {

    val libName = mod.sharedLibName()
    if (libName in loadedLibraries) return

    val tempDir = Files.createTempDirectory("tempSharedLibFor_$libName")
    tempDir.toFile().deleteOnExit()
    val tempSharedLib = tempDir.resolve(libName)
    resourceLoader.withResourceStream("$NATIVE_RESOURCE_FOLDER_NAME/$libName") { stream ->
        stream!!
        val channel =
            Files.newByteChannel(
                tempSharedLib, StandardOpenOption.WRITE, StandardOpenOption.CREATE
            )
        channel.use {
            Files.copy(stream, tempSharedLib, StandardCopyOption.REPLACE_EXISTING)
        }
    }
    /*

    I MUST control the classloader the library gets associated with in a safe way, because unloading is only possible if the classloader is not the app classloader and because I need to have strict control of the unloading process (library gets unloaded after the classloader associated with it is GCed).

     */
    load0.invoke(
        Runtime.getRuntime(),
        classLoaderOfClass.java,
        tempSharedLib.pathString
    )
    loadedLibraries += libName
}

/*
[[java.lang.System#load]]
[[java.lang.Runtime#load0]]
*/
private val load0: Method by lazy {
    /*
    Unlocked by addOpens argument in [[KJvmArgsSets#FOR_NEW_MAC]]
     */
    val method =
        Runtime::class.java.getDeclaredMethod(
            "load0",
            Class::class.java,
            String::class.java
        )
    method.isAccessible = true
    method
}


