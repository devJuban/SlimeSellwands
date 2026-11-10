plugins {
    id("java")
}

repositories {
    maven("https://repo.codemc.io/repository/bentoboxworld/")
}

dependencies {
    compileOnly("world.bentobox:bentobox:3.10.1-SNAPSHOT")
    compileOnly("world.bentobox:bank:1.9.0-SNAPSHOT")
    compileOnly("com.google.guava:guava:31.1-jre")

//    {
//        exclude("com.github.Maxlego08", "zMenu-API")
//    }
}
