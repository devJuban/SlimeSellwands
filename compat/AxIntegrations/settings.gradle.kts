rootProject.name = "AxIntegrations"

includeBuild("../AxAPI")

rootDir.resolve("integrations")
    .listFiles { file -> file.isDirectory }
    ?.forEach { dir ->
        include("integrations:${dir.name}")
    }

include("common")
include("legacy")
