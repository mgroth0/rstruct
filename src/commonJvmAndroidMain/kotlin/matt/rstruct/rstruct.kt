
package matt.rstruct

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import matt.collect.itr.subList
import matt.file.commons.fnames.FileNames.CHANGELIST_MD
import matt.file.construct.common.mFile
import matt.lang.anno.SeeURL
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.MacDefaultFileSystem
import matt.lang.model.file.UnixFileSystem
import matt.log.report.VersionGetterService
import matt.model.code.mod.RelativeToKMod
import matt.model.data.release.Version
import matt.rstruct.loader.ResourceLoader
import matt.rstruct.loader.readResourceAsText

internal fun ResourceLoader.modId() =
    run {
        Json.decodeFromString<ModId>(
            readResourceAsText(modIdFileRelativeToResources.path)
                ?: error("${modIdFileRelativeToResources.path} not found")
        )
    }

fun ResourceLoader.unsafeExtraValues() =
    run {
        Json.decodeFromString<Map<String, String>>(
            readResourceAsText(valuesFileRelativeToResources.path)
                ?: error("${valuesFileRelativeToResources.path} not found")
        )
    }


@SeeURL("https://www.eclipse.org/forums/index.php/t/71872/")
data object ResourceFileSystem : UnixFileSystem() {
    override val caseSensitivity = CaseSensitive
}

const val ASSETS_FOLDER_NAME = "assets"

/*needed for assets manager*/
val assetsFolderRelativeToResources by lazy { mFile(ASSETS_FOLDER_NAME, ResourceFileSystem) }

val appNameFileRelativeToResources by lazy { mFile("matt", ResourceFileSystem)["appname.txt"] }
val modIdFileRelativeToResources by lazy {
    mFile("matt", ResourceFileSystem)["mod-id.json"] /*changed modID to mod-id to avoid casing issues... resources are case sensitive even on mac*/
}
val valuesFileRelativeToResources by lazy { assetsFolderRelativeToResources["matt"]["values.json"] }

val changelistFileRelativeToResources by lazy { mFile(CHANGELIST_MD, MacDefaultFileSystem) }
val appNameFileRelativeToSourceSet by lazy { mFile("resources", MacDefaultFileSystem)[appNameFileRelativeToResources.path] }
val modIDFileRelativeToSourceSet by lazy { mFile("resources", MacDefaultFileSystem)[modIdFileRelativeToResources.path] }


val changelistFileRelativeToSourceSet by lazy {
    mFile(
        "resources",
        MacDefaultFileSystem
    )[changelistFileRelativeToResources.path]
}

val valuesFileRelativeToSourceSet by lazy { mFile("resources", MacDefaultFileSystem)[valuesFileRelativeToResources.path] }


class VersionGetterServiceThingImpl(resourceLoader: ResourceLoader) : VersionGetterService {
    val version by lazy {
        resourceLoader.modId().version
    }

    override fun getTheVersion(): String = version.toString()
}

@Serializable
class ModId(
    val appName: String,
    val gradlePath: String,
    val version: Version
) : RelativeToKMod {
    override val relToKNames: List<String> get() = gradlePath.split(":").filter { it.isNotBlank() }.subList(1)
}

internal fun ResourceLoader.appName() =
    run {
        modId().appName
    }
