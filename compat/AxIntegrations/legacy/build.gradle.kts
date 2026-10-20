plugins {
    id("java")
}

group = "com.artillexstudios.axintegrations"
version = rootProject.version

sourceSets {
    main {
        java {
            srcDir(rootProject.file("common/src/main/java"))
            srcDir(rootProject.file("integrations/vault/src/main/java"))
            srcDir(rootProject.file("integrations/vanilla/src/main/java"))
        }
    }
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.withType<JavaCompile> {
    options.release.set(8)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
