
package matt.rstruct.loader

import matt.collect.props.Properties
import matt.file.JioFile
import matt.file.construct.mFile
import matt.lang.classpathwork.j.ClassPathWorker
import matt.model.code.sys.LinuxFileSystem
import matt.prim.str.elementsToString
import java.io.InputStream
import java.net.URL


interface ResourceLoader {
    fun resourceStream(name: String): InputStream?
}


fun <R> ResourceLoader.withResourceStream(
    name: String,
    op: (InputStream?) -> R
) = resourceStream(name).use { s ->
    op(s)
}

fun ResourceLoader.readResourceAsProperties(name: String) = Properties(resourceStream(name)!!)


/*Thing()::class.java.classLoader


ClassLoader.getPlatformClassLoader()*/
fun ResourceLoader.readResourceAsText(name: String): String? =
    withResourceStream(name) {
        it?.bufferedReader()?.readText()
    }


class ClassicResourceLoader(vararg classLoaders: ClassLoader) : ClassPathWorker(*classLoaders), ResourceLoader {

    private fun resources(name: String): Sequence<URL> =
        sequence {
            classLoaders.forEach {
                yieldAll(it.getResources(name).asSequence())
            }
        }

    /*It's a unix style path on all systems*/
    override fun resourceStream(name: String): InputStream? {
        val checkedSingleURL = resourceURL(name)
        return checkedSingleURL?.openStream()
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

    fun resourceFile(path: String) =
        resourceURL(path)?.toURI()?.let {
            mFile(
                it.path,
                LinuxFileSystem /*jar resources are always case-sensitive, even if a host that is case-insensitive*/
            )
        }
}
