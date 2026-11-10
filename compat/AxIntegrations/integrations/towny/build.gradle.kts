plugins {
    id("java")
}

repositories {
    maven("https://repo.glaremasters.me/repository/towny/")
}

dependencies {
    compileOnly("com.palmergames.bukkit.towny:towny:0.102.0.12") {
        // Avoid pulling the Java 21 Adventure API variant into a Java 8 build.
        exclude(group = "net.kyori", module = "adventure-api")
    }
}
