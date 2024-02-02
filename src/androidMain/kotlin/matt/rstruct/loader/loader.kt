package matt.rstruct.loader

import android.content.res.AssetManager
import matt.rstruct.ASSETS_FOLDER_NAME
import java.io.FileNotFoundException
import java.io.InputStream


class AssetResourceLoader(
    private val assetManager: AssetManager
) : ResourceLoader {
    override fun resourceStream(name: String): InputStream? {
        val prefix = "$ASSETS_FOLDER_NAME/"
        check(name.startsWith(prefix))
        try {
            return assetManager.open(name.removePrefix(prefix))
        } catch (e: FileNotFoundException) {
            return null
        }
    }
}
