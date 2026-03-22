pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "2.0.140"
        id("net.neoforged.moddev.repositories") version "2.0.140"
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven {
            url = uri("https://maven.parchmentmc.org")
        }

        maven {
            name = "FirstDarkDev"
            url = uri("https://maven.firstdark.dev/releases")
        }

        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
            content {
                includeGroup("net.neoforged")
            }
        }
    }
}

rootProject.name = "EnderIO"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    id("net.neoforged.moddev.repositories")
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    rulesMode = RulesMode.FAIL_ON_PROJECT_RULES

    repositories {
        maven {
            name = "Rover656 Maven"
            url = uri("https://maven.rover656.dev/releases")
            content {
                includeGroup("com.enderio")

                // Mirrors
                includeGroup("dev.gigaherz.graph")
            }
        }

        maven {
            name = "ModMaven"
            url = uri("https://modmaven.dev")
            content {
                includeGroup("mezz.jei")
                includeGroup("mcjty.theoneprobe")
                includeGroup("appeng")
                includeGroup("mekanism")
            }
        }

        maven {
            name = "SquidDev Maven"
            url = uri("https://maven.squiddev.cc")
            content {
                includeGroup("cc.tweaked")
            }
        }

        maven {
            name = "Jared's Maven"
            url = uri("https://maven.blamejared.com")
            content {
                includeGroup("vazkii.patchouli")
                includeGroup("net.darkhax.bookshelf")
                includeGroup("net.darkhax.enchdesc")
                includeGroup("com.almostreliable.mods")
            }
        }

        maven {
            name = "Curse Maven"
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }

        maven {
            name = "Modrinth Maven"
            url = uri("https://api.modrinth.com/maven")
            content {
                includeGroup("maven.modrinth")
            }
        }

        maven {
            name = "FTB Maven"
            url = uri("https://maven.ftb.dev/releases")
            content {
                includeGroup("dev.ftb.mods")
            }
        }

        maven {
            name = "Architectury Maven"
            url = uri("https://maven.architectury.dev/")
            content {
                includeGroup("dev.architectury")
            }
        }

        maven {
            url = uri("https://maven.creeperhost.net/release")
            content {
                includeGroup("com.refinedmods.refinedstorage")
            }
        }

        maven {
            name = "Maven for PR #2879" // https://github.com/neoforged/NeoForge/pull/2879
            url = uri("https://prmaven.neoforged.net/NeoForge/pr2879")
            content {
                includeModule("net.neoforged", "neoforge")
                includeModule("net.neoforged", "testframework")
            }
        }

        mavenLocal() {
            content {
                includeGroup("com.enderio")
                includeGroup("net.neoforged")
            }
        }
    }
}

include("ensure_plugin")
include("endercore")
include("enderio")
//include("enderio-modded-conduits")
include("enderio-endergy")
