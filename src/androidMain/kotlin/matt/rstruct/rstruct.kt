package matt.rstruct

import android.content.res.AssetManager
import matt.rstruct.loader.AssetResourceLoader


fun AssetManager.modId() = run {
    AssetResourceLoader(this).modId()
}

fun AssetManager.extraValues() = run {
    AssetResourceLoader(this).unsafeExtraValues()
}


fun AssetManager.appName() = run {
    AssetResourceLoader(this).appName()
}