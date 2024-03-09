package matt.rstruct.a

import android.content.res.AssetManager
import matt.rstruct.appName
import matt.rstruct.loader.a.AssetResourceLoader
import matt.rstruct.modId
import matt.rstruct.unsafeExtraValues


fun AssetManager.modId() =
    run {
        AssetResourceLoader(this).modId()
    }

fun AssetManager.extraValues() =
    run {
        AssetResourceLoader(this).unsafeExtraValues()
    }


fun AssetManager.appName() =
    run {
        AssetResourceLoader(this).appName()
    }
