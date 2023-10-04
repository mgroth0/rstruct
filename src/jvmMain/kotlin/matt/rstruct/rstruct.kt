package matt.rstruct

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import matt.collect.itr.subList
import matt.file.commons.CHANGELIST_MD
import matt.file.construct.mFile
import matt.lang.anno.SeeURL
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.UnixFileSystem
import matt.log.report.VersionGetterService
import matt.model.code.mod.RelativeToKMod
import matt.model.data.release.Version
import matt.rstruct.loader.systemResourceLoader


//@Deprecated("It is unclear how this will work in plugin classloaders")
/*val appName by lazy { resourceTxt(appNameFileRelativeToResources.path) ?: "app_name_blank" }*/
val modID by lazy {
    Json.decodeFromString<ModID>(
        systemResourceLoader().readResourceAsText(modIDFileRelativeToResources.path)
            ?: error("${modIDFileRelativeToResources.path} not found")
    )
}

//@Deprecated("It is unclear how this will work in plugin classloaders")
/*val appName by lazy { resourceTxt(appNameFileRelativeToResources.path) ?: "app_name_blank" }*/
val extraValues by lazy {
    Json.decodeFromString<Map<String, String>>(
        systemResourceLoader().readResourceAsText(valuesFileRelativeToResources.path)
            ?: error("${valuesFileRelativeToResources.path} not found")
    )
}


@SeeURL("https://www.eclipse.org/forums/index.php/t/71872/")
data object ResourceFileSystem : UnixFileSystem() {
    override val caseSensitivity = CaseSensitive
}

val appNameFileRelativeToResources by lazy { mFile("matt", ResourceFileSystem)["appname.txt"] }
val modIDFileRelativeToResources by lazy { mFile("matt", ResourceFileSystem)["modID.json"] }
val valuesFileRelativeToResources by lazy { mFile("matt", ResourceFileSystem)["values.json"] }

val changelistFileRelativeToResources by lazy { mFile(CHANGELIST_MD, MacFileSystem) }
val appNameFileRelativeToSourceSet by lazy { mFile("resources", MacFileSystem)[appNameFileRelativeToResources.path] }
val modIDFileRelativeToSourceSet by lazy { mFile("resources", MacFileSystem)[modIDFileRelativeToResources.path] }
val changelistFileRelativeToSourceSet by lazy {
    mFile(
        "resources",
        MacFileSystem
    )[changelistFileRelativeToResources.path]
}

val valuesFileRelativeToSourceSet by lazy { mFile("resources", MacFileSystem)[valuesFileRelativeToResources.path] }


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