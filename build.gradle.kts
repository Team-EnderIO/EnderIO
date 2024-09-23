import java.net.URI

plugins {
    id("maven-publish")
    //id("com.modrinth.minotaur") version "2.+"
    id("net.neoforged.moddev") version "1.0.14" apply false
    //id("com.hypherionmc.modutils.modpublisher") version "2.+"
    //id("checkstyle")
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

/*checkstyle {
    isIgnoreFailures = false
}*/


//if (getReleaseType() != null) {
//    if (System.getenv("CHANGELOG") != null) {
//        publisher {
//
//            apiKeys {
//                curseforge(System.getenv("CURSEFORGE_TOKEN"))
//                modrinth(System.getenv("MODRINTH_TOKEN"))
//            }
//
//            debug.set(System.getenv("PUBLISH") != "true")
//
//            curseID.set(curseforge_projectId)
//            modrinthID.set(modrinth_projectId)
//            versionType.set(getReleaseType())
//            version.set(mod_version)
//            displayName.set("Ender IO - $mod_version")
//            changelog.set(System.getenv("CHANGELOG"))
//            gameVersions.set(listOf(minecraft_version))
//            loaders.set(listOf("neoforge"))
//            curseEnvironment.set("both")
//            artifact.set(tasks.jarJar)
//
//            setJavaVersions("Java 17")
//
//            curseDepends {
//                optional("jei", /*"patchouli",*/ "athena", "applied-energistics-2", "mekanism", "cc-tweaked")
//            }
//
//            modrinthDepends {
//                optional(/*modrinth_dep_patchouli, */ modrinth_dep_jei, modrinth_dep_athena, modrinth_dep_ae2, modrinth_dep_mekanism, modrinth_dep_cct)
//            }
//        }
//    } else {
//        println("Release disabled, no changelog found in environment");
//    }
//}
//
//tasks.withType<JavaCompile> {
//    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
//    if (group != null) {
//        return@withType; // neoform recompile
//    }
//    if (name == "compileEnsure_pluginJava") {
//        //don't use the plugin to compile the plugin and open the required packages to compile it correctly, the packages are opened at compile time for other modules using EnsureSetup
//        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.api=ensureplugin")
//        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.code=ensureplugin")
//        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.tree=ensureplugin")
//        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.util=ensureplugin")
//    } else if (name != "compileJava") {
//        //all modules except an unnamed one (not sure what this one is tbh)
//        options.compilerArgs.add("-Xplugin:ContextEnsure")
//    }
//}

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
