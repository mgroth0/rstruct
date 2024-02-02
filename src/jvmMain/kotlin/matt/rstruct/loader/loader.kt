package matt.rstruct.loader


/*because it should be explict where I am getting the resources from now that I am starting to use plugin classloaders*/
/*this is not in the android source set because an AssetResourceLoader should be used instead*/
fun systemResourceLoader() = ClassicResourceLoader(
    ClassLoader.getSystemClassLoader()
)
