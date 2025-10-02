import java.net.URI

val libs = versionCatalogs.named("libs")

plugins {
    `java-library`
    `maven-publish`
}

apply(plugin = libs.findPlugin("moddev").get().get().pluginId)

group = "com.enderio"

val versionSeries: String by project
val buildVersion = System.getenv("BUILD_VERSION")
if (buildVersion != null) {
    version = buildVersion
} else {
    version = "$versionSeries-dev"
}

repositories {
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
            url = uri("https://maven.pkg.github.com/refinedmods/refinedstorage2")
            credentials {
                username = "anything"
                password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
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

dependencies {
    compileOnly(libs.findLibrary("jetbrainsAnnotations").get())

    // Depend on ensure compiler plugin
    compileOnly(project(":ensure_plugin"))
    annotationProcessor(project(":ensure_plugin"))
    testCompileOnly(project(":ensure_plugin"))
    testAnnotationProcessor(project(":ensure_plugin"))

    testImplementation(libs.findLibrary("junitJupiter").get())
    testRuntimeOnly(libs.findLibrary("junitPlatformLauncher").get())
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Use ensure plugin
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xplugin:ContextEnsure")
    options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "400"))
}

//tasks.test {
//    useJUnitPlatform()
//}

publishing {
    repositories {
        if (System.getenv("RVR_MAVEN_USER") != null) {
            maven {
                name = "Rover656"
                url = URI("https://maven.rover656.dev/releases")
                credentials {
                    username = System.getenv("RVR_MAVEN_USER")
                    password = System.getenv("RVR_MAVEN_PASSWORD")
                }
            }
        }
    }
}
