import java.net.URI

plugins {
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.88" apply false
    id("idea")
}

println("Release type: ${getReleaseType()}")

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allprojects {
    gradle.projectsEvaluated {
        tasks.withType<JavaCompile> {
            options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "400"))
        }
    }
}

// Normally we'd do this in the shared buildSrc file
// However, we need this context before loading subprojects or stuff explodes.
subprojects {
    if (project.name != "ensure_plugin") {
        apply(plugin = "maven-publish")
        apply(plugin = "net.neoforged.moddev")

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
    }
}

// ============
// Utilities
// ============

fun getReleaseType(): String? {
    // If we"re doing a proper build
    if (System.getenv("BUILD_VERSION") != null) {
        val version_string = System.getenv("BUILD_VERSION")

        if (version_string.lowercase().contains("alpha")) {
            return "alpha"
        } else if (version_string.lowercase().contains("beta")) {
            return "beta"
        }

        return "release"
    }

    return "dev"
}
