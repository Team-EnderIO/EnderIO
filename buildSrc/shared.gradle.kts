group = "com.enderio"

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
    // TODO: Once EnderCore moves out, put common mods that we test alongside in here?
    add("compileOnly", "org.jetbrains:annotations:23.0.0")

    if (project.name != "ensure_plugin") {
        add("compileOnly", project(":ensure_plugin"))
        add("annotationProcessor", project(":ensure_plugin"))
        add("testCompileOnly", project(":ensure_plugin"))
        add("testAnnotationProcessor", project(":ensure_plugin"))

        if (project.name != "endercore") {
            add("api", project(":endercore"))
        }
    }
}

if (project.name != "ensure_plugin") {
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xplugin:ContextEnsure")
    }
}


version = getVersionString()

val minecraftVersionRange: String by project
val neoForgeVersionRange: String by project
val loaderVersionRange: String by project

// TODO: Doing this here temporarily, but should be done in separate projects instead of here tbh.
val mekanismVersionRange: String by project
val ae2VersionRange: String by project
val refinedstorageVersionRange: String by project
val ftbUltimineVersionRange: String by project

val replaceProperties = mapOf(
        "mod_version" to project.version,
        "mcversion" to minecraftVersionRange,
        "neo_version" to neoForgeVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mekanism_version_range" to mekanismVersionRange,
        "ae2_version_range" to ae2VersionRange,
        "refinedstorage_version_range" to refinedstorageVersionRange,
        "ftbUltimine_version_range" to ftbUltimineVersionRange,
)

tasks.withType<ProcessResources>().configureEach {
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
        expand(mutableMapOf("project" to project))
    }
}

// ============
// Utilities
// ============

fun shellRunAndRead(command: String): String {
    val process = ProcessBuilder()
            .command(command.split(" "))
            .directory(rootProject.projectDir)
            .start()
    return process.inputStream.bufferedReader().readText()
}

//   * enderio-7.0.1-alpha.jar      :: release version 7.0.1-alpha (discovered by git tag)
//   * enderio-7.0.1.349-nightly    :: nightly build no. 349, based after 7.0.1.
//   * enderio-7.0-dev+c91c8ee6e    :: dev (local) build for commit c91c8ee6e for version set 7.0.
fun getVersionString(): String {
    if (System.getenv("BUILD_VERSION") != null) {
        var buildVersion = System.getenv("BUILD_VERSION")
        if (buildVersion.startsWith("v")) {
            buildVersion = buildVersion.substring(1)
        }

        return buildVersion
    }

    val versionSeries: String by project

    // If this is not a release, we're going to get the last tag, add the ci build number, then append -dev+<commit_hash>
    var commitHash = shellRunAndRead("git rev-parse --short HEAD").trim();
    var previousTagVersion = shellRunAndRead("git describe --tags --abbrev=0").trim();

    // Extract the numeric component of the last version.
    var versionRegex = Regex("""\d+(\.\d+)+""")
    var currentVersion = versionRegex.find(previousTagVersion)?.value
    if (currentVersion == null) {
        // Fallback to version series if we're unable to discover the previous version.
        currentVersion = "$versionSeries.0"
    }

    if (System.getenv("BUILD_NUMBER") != null) {
        val buildNumber = System.getenv("BUILD_NUMBER")
        return "$currentVersion.$buildNumber-nightly+$commitHash"
    }

    return "$versionSeries-dev+$commitHash"
}
