plugins {
    id("java")
}

repositories {
    maven("https://dependency.download/releases")
}

dependencies {
    compileOnly("dev.kitteh:factions:4.5.1") {
        // The provider's modern Adventure variant requires Java 21. This
        // integration only compiles against Bukkit-facing Factions methods.
        exclude(group = "net.kyori", module = "adventure-api")
    }
}
