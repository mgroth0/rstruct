package matt.rstruct

import matt.rstruct.loader.systemResourceLoader


val modId by lazy {
    systemResourceLoader().modId()
}

val extraValues by lazy {
    systemResourceLoader().unsafeExtraValues()
}


val appName by lazy {
    systemResourceLoader().appName()
}