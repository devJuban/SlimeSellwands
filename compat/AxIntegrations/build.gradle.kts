plugins {
    id("java")
    id("com.gradleup.shadow") version("9.3.1")
    `maven-publish`
}

group = "com.artillexstudios.axintegrations"
version = "35"

allprojects {
    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.artillex-studios.com/releases/")
        maven("https://jitpack.io/")
    }

    dependencies {
        val spigotApiJar = providers.gradleProperty("spigotApiJar").orNull
        val defaultSpigotApi = if (project.name == "legacy") "org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT" else "org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT"
        if (spigotApiJar == null) {
            compileOnly(defaultSpigotApi)
        } else {
            compileOnly(files(spigotApiJar))
        }
        compileOnly("com.artillexstudios.axapi.api:api:2.1.8")
        compileOnly(files(rootProject.file("../AxAPI/paper/build/libs/paper-2.1.8.jar")))
        compileOnly("net.kyori:adventure-api:4.25.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    }

    configurations.configureEach {
        exclude(group = "com.artillexstudios.axapi", module = "axapi")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.release.set(8)
    }
}

tasks {
    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()

        subprojects.forEach { sub ->
            from(sub.sourceSets.main.get().output)
        }
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.artillexstudios.axintegrations"
            artifactId = "AxIntegrations"
            version = "$version"

            artifact(tasks.shadowJar.get())
        }
    }

    repositories {
        maven {
            name = "Artillex-Studios"
            url = uri("https://repo.artillex-studios.com/releases/")
            credentials {
                username = (findProperty("maven_username") as String?) ?: System.getenv("MAVEN_USERNAME")
                password = (findProperty("maven_password") as String?) ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
