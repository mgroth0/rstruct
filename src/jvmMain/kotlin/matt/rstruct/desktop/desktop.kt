package matt.rstruct.desktop

import matt.rstruct.appName
import matt.rstruct.loader.desktop.systemResourceLoader
import matt.rstruct.modId
import matt.rstruct.unsafeExtraValues


val modId by lazy {
    systemResourceLoader().modId()
}

val extraValues by lazy {
    systemResourceLoader().unsafeExtraValues()
}


val appName by lazy {
    systemResourceLoader().appName()
}
