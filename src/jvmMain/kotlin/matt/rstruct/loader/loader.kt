package matt.rstruct.loader

import matt.file.JioFile
import matt.file.construct.mFile
import matt.file.guessRuntimeFileSystem
import matt.lang.classpathwork.ClassPathWorker
import matt.prim.str.elementsToString
import java.io.InputStream
import java.net.URL

/*because it should be explict where I am getting the resources from now that I am starting to use plugin classloaders*/
fun systemResourceLoader() = ResourceLoader(ClassLoader.getSystemClassLoader())

class ResourceLoader(vararg classLoaders: ClassLoader) : ClassPathWorker(*classLoaders) {

    private fun resources(name: String): Sequence<URL> = sequence {
        classLoaders.forEach {
            yieldAll(it.getResources(name).asSequence())
        }
    }

    /*It's a unix style path on all systems*/
    fun resourceStream(name: String): InputStream? {
        val checkedSingleURL = resourceURL(name)
        return checkedSingleURL?.openStream()
    }


    /*Thing()::class.java.classLoader*/
    /*ClassLoader.getPlatformClassLoader()*/
    fun readResourceAsText(name: String): String? {
        return withResourceStream(name) {
            it?.bufferedReader()?.readText()
        }
    }


    fun <R> withResourceStream(
        name: String,
        op: (InputStream?) -> R
    ) = resourceStream(name).use { s ->
        op(s)
    }

    /*It's a unix style path on all systems*/
    fun resourceURL(name: String): URL? =
        resources(name.replace("\\", JioFile.unixSeparator)).toList().let {


            if (it.isEmpty()) null
            else if (it.size == 1) it.single()
            else {
                println("there are multiple resources with the name $name: ${it.elementsToString()}") /*because for some reasons the error message below is currently not printing*/
                error("there are multiple resources with the name $name: ${it.elementsToString()} This will lead to bugs as it becomes ambiguous which one will be selected. Technically it is based on the classpath ordering, but that is something I want my code to be invariant to.")
            }
        }

    fun resourceFile(path: String) = resourceURL(path)?.toURI()?.let { mFile(it.path, guessRuntimeFileSystem) }


}