package matt.rstruct.loader.a

import android.content.res.AssetManager
import matt.lang.fnf.runCatchingFileTrulyNotFound
import matt.rstruct.ASSETS_FOLDER_NAME
import matt.rstruct.loader.ResourceLoader
import java.io.InputStream


class AssetResourceLoader(
    private val assetManager: AssetManager
) : ResourceLoader {
    override fun resourceStream(name: String): InputStream? {
        val prefix = "$ASSETS_FOLDER_NAME/"
        check(name.startsWith(prefix))

        return  runCatchingFileTrulyNotFound(
            file  = { error("asset file...") }
        ) {
            assetManager.open(name.removePrefix(prefix))
        }.getOrNull()
    }
}
