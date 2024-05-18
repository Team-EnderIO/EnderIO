pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.parchmentmc.org")
        maven("https://maven.firstdarkdev.xyz/releases")
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "EnderIO"
