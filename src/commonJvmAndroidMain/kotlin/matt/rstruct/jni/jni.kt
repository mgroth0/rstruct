package matt.rstruct.jni

import matt.model.code.mod.RelativeToKMod
import matt.model.code.mod.sharedLibBaseName
import matt.rstruct.loader.ClassicResourceLoader
import matt.rstruct.loader.withResourceStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import kotlin.io.path.pathString


const val NATIVE_RESOURCE_FOLDER_NAME = "native"
fun RelativeToKMod.sharedLibName() = "lib${sharedLibBaseName}.dylib"
/*fun sharedLibResource(mod: RelativeToKMod) = systemResourceLoader().readResourceAsText()*/

private val loadedLibraries = mutableSetOf<String>()


@Suppress("UnsafeDynamicallyLoadedCode")
/**
 * Completely SAFE dynamically loaded code. SAFER than every alternative I can imagine. The SAFEST option possible.
 */
@Synchronized
fun loadSharedLibraryResource(
    resourceLoader: ClassicResourceLoader,
    mod: RelativeToKMod
) {

    val libName = mod.sharedLibName()



    if (libName in loadedLibraries) return

    val tempDir = Files.createTempDirectory("tempSharedLibFor_$libName")
    val tempSharedLib = tempDir.resolve(libName)
    resourceLoader.withResourceStream("$NATIVE_RESOURCE_FOLDER_NAME/$libName") { stream ->
        stream!!
        val channel = Files.newByteChannel(
            tempSharedLib, StandardOpenOption.WRITE, StandardOpenOption.CREATE
        )
        channel.use {
            Files.copy(stream, tempSharedLib, StandardCopyOption.REPLACE_EXISTING)
        }
    }
    System.load(tempSharedLib.pathString)
    loadedLibraries += libName
}

