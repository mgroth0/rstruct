package matt.rstruct

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import matt.collect.itr.subList
import matt.file.MFile
import matt.file.commons.CHANGELIST_MD
import matt.file.construct.mFile
import matt.log.report.VersionGetterService
import matt.model.code.mod.RelativeToKMod
import matt.model.data.release.Version
import matt.prim.str.elementsToString
import java.io.InputStream
import java.net.URL


/*val appName by lazy { resourceTxt(appNameFileRelativeToResources.path) ?: "app_name_blank" }*/
val modID by lazy {
    Json.decodeFromString<ModID>(
        resourceTxt(modIDFileRelativeToResources.path) ?: error("${modIDFileRelativeToResources.path} not found")
    )
}

/*val appName by lazy { resourceTxt(appNameFileRelativeToResources.path) ?: "app_name_blank" }*/
val extraValues by lazy {
    Json.decodeFromString<Map<String, String>>(
        resourceTxt(valuesFileRelativeToResources.path) ?: error("${valuesFileRelativeToResources.path} not found")
    )
}

/*Thing()::class.java.classLoader*/
/*ClassLoader.getPlatformClassLoader()*/
fun resourceTxt(name: String): String? {
    return withResourceStream(name) {
        it?.bufferedReader()?.readText()
    }
}

private fun systemClassLoader(): ClassLoader = ClassLoader.getSystemClassLoader()

/*It's a unix style path on all systems*/
fun resourceURL(name: String): URL? =
    resources(name.replace("\\", MFile.unixSeparator)).toList().let {

        if (it.isEmpty()) null
        else if (it.size == 1) it.single()
        else {
            println("there are multiple resources with the name $name: ${it.elementsToString()}") /*because for some reasons the error message below is currently not printing*/
            error("there are multiple resources with the name $name: ${it.elementsToString()} This will lead to bugs as it becomes ambiguous which one will be selected. Technically it is based on the classpath ordering, but that is something I want my code to be invariant to.")
        }
    }

/*It's a unix style path on all systems*/
fun resourceStream(name: String): InputStream? {
    val checkedSingleURL = resourceURL(name)
    return checkedSingleURL?.openStream()
}

fun <R> withResourceStream(
    name: String,
    op: (InputStream?) -> R
) = resourceStream(name).use { s ->
    op(s)
}


private fun resources(name: String): Sequence<URL> = systemClassLoader().getResources(name).asSequence()


fun resourceFile(path: String) = resourceURL(path)?.toURI()?.let { mFile(it) }

val appNameFileRelativeToResources by lazy { mFile("matt")["appname.txt"] }
val modIDFileRelativeToResources by lazy { mFile("matt")["modID.json"] }
val valuesFileRelativeToResources by lazy { mFile("matt")["values.json"] }
val changelistFileRelativeToResources by lazy { mFile(CHANGELIST_MD) }
val appNameFileRelativeToSourceSet by lazy { mFile("resources")[appNameFileRelativeToResources] }
val modIDFileRelativeToSourceSet by lazy { mFile("resources")[modIDFileRelativeToResources] }
val changelistFileRelativeToSourceSet by lazy { mFile("resources")[changelistFileRelativeToResources] }

val valuesFileRelativeToSourceSet by lazy { mFile("resources")[valuesFileRelativeToResources] }


class VersionGetterServiceThingImpl : VersionGetterService {
    override fun getTheVersion(): String {
        return modID.version.toString()
    }

}

@Serializable
class ModID(
    val appName: String,
    val gradlePath: String,
    val version: Version
) : RelativeToKMod {
    override val relToKNames: List<String> get() = gradlePath.split(":").filter { it.isNotBlank() }.subList(1)
}

val appName by lazy {
    modID.appName
}