package matt.rstruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import matt.collect.itr.subList
import matt.file.MFile
import matt.file.commons.CHANGELIST_MD
import matt.file.construct.mFile
import matt.log.report.VersionGetterService
import matt.model.code.mod.RelativeToKMod
import matt.model.data.release.Version
import java.io.InputStream
import java.net.URL


/*val appName by lazy { resourceTxt(appNameFileRelativeToResources.path) ?: "app_name_blank" }*/
val modID by lazy {
    Json.decodeFromString<ModID>(
        resourceTxt(modIDFileRelativeToResources.path) ?: error("${modIDFileRelativeToResources.path} not found")
    )
}

/*Thing()::class.java.classLoader*/
/*ClassLoader.getPlatformClassLoader()*/
fun resourceTxt(name: String): String? {
    return resourceStream(name)?.bufferedReader()?.readText()
}

/*It's a unix style path on all systems*/
fun resourceURL(name: String): URL? =
    ClassLoader.getSystemClassLoader().getResource(name.replace("\\", MFile.unixSeperator))

/*It's a unix style path on all systems*/
fun resourceStream(name: String): InputStream? =
    ClassLoader.getSystemClassLoader().getResourceAsStream(name.replace("\\", MFile.unixSeperator))


val appNameFileRelativeToResources by lazy { mFile("matt")["appname.txt"] }
val modIDFileRelativeToResources by lazy { mFile("matt")["modID.json"] }
val changelistFileRelativeToResources by lazy { mFile(CHANGELIST_MD) }
val appNameFileRelativeToSourceSet by lazy { mFile("resources")[appNameFileRelativeToResources] }
val modIDFileRelativeToSourceSet by lazy { mFile("resources")[modIDFileRelativeToResources] }
val changelistFileRelativeToSourceSet by lazy { mFile("resources")[changelistFileRelativeToResources] }


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