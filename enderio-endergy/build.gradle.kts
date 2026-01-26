import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("mod-common-conventions")
//    alias(libs.plugins.modpublisher)
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }

    create("datagen") {
        compileClasspath += sourceSets.main.get().output
    }
}

configurations {
    val localRuntime by creating

    runtimeClasspath {
        extendsFrom(localRuntime)
    }

    testImplementation {
        extendsFrom(implementation.get())
    }

    testCompileOnly {
        extendsFrom(compileOnly.get())
    }


    testRuntimeOnly {
        // TODO: Mekanism breaks our unit tests...
        exclude(group = "mekanism", module = "Mekanism")
        extendsFrom(runtimeOnly.get())
    }

    testAnnotationProcessor {
        extendsFrom(annotationProcessor.get())
    }

    val datagenImplementation by getting {
        extendsFrom(implementation.get())
    }

    val datagenCompileOnly by getting {
        extendsFrom(compileOnly.get())
    }

    val datagenRuntimeOnly by getting {
        extendsFrom(runtimeOnly.get())
    }

    val datagenAnnotationProcessor by getting {
        extendsFrom(annotationProcessor.get())
    }
}



dependencies {
    // Include and bundle regilite
    api(libs.regilite)
    jarJar(libs.regilite)

    // EnderIO will bundle Regilite and EnderCore in production.
    api(project(":enderio"))
}

neoForge {
    enable {
        version = libs.versions.neoforge.get()
        isDisableRecompilation = System.getenv("CI") == "true"
    }

    addModdingDependenciesTo(sourceSets.getByName("datagen"))

    val modEndergy by mods.creating {
        sourceSet(sourceSets.getByName("datagen"))
        sourceSet(sourceSets.getByName("main"))
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
        }

        val data by creating {
            data()

            programArguments.addAll(
                    "--mod", "enderio_endergy",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }
    }

    unitTest {
        enable()
        testedMod = modEndergy
    }
}

// Expand variables in mods.toml
var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
            "mod_version" to project.version,
            "minecraft_version_range" to libs.versions.minecraft.get(),
            "neoforge_version" to libs.versions.neoforge.get(),
            "loader_version_range" to "[4,)", // TODO
    )

    inputs.properties(replaceProperties)
    into("build/generated/sources/modMetadata")

    from("src/main/templates") {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(replaceProperties)
        }
    }

    from("${rootDir}/docs/img/") {
        include("logo.png")
    }
}

// Add results to source set and to IDE sync
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO - Endergy",
                "Specification-Vendor" to "Team Ender IO",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Team Ender IO",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        ))
    }
}

tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")

    from(sourceSets["main"].output)
    from(sourceSets["main"].allJava)
    include("com/enderio/enderio/api/**")
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allJava)
}

tasks.build {
    dependsOn(tasks["apiJar"])
    dependsOn(tasks["sourcesJar"])
}

val curseforge_projectId: String by project
val modrinth_projectId: String by project

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
//
//            versionType.set(getReleaseType())
//            projectVersion.set("${project.version}")
//
//            displayName.set("Ender IO - ${project.version}")
//            changelog.set(System.getenv("CHANGELOG"))
//
//            setGameVersions("1.21.1")
//            setLoaders(ModLoader.NEOFORGE)
//
//            curseEnvironment.set("both")
//            artifact.set(tasks.jar)
//
//            setJavaVersions(JavaVersion.VERSION_21)
//
//            curseDepends {
//                optional("jei", "athena", "applied-energistics-2", "mekanism", "cc-tweaked")
//            }
//
//            modrinthDepends {
//                optional("jei", "athena-ctm", "ae2", "mekanism", "cc-tweaked")
//            }
//        }
//    } else {
//        println("Release disabled, no changelog found in environment");
//    }
//}

fun getReleaseType(): String {
    // If we"re doing a proper build
    if (System.getenv("BUILD_VERSION") != null) {
        val versionString = System.getenv("BUILD_VERSION").lowercase()

        if (versionString.contains("alpha")) {
            return "alpha"
        } else if (versionString.contains("beta")) {
            return "beta"
        }

        return "release"
    }

    return "dev"
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = "com.enderio"
            artifactId = project.name
            version = "${project.version}"

            from(components["java"])
            artifact(tasks["apiJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO - Endergy")
                description.set("Ender IO - Endergy")
                url.set("https://github.com/Team-EnderIO/EnderIO")

                licenses {
                    license {
                        name.set("Unlicense")
                        url.set("https://github.com/Team-EnderIO/EnderIO/blob/dev/1.21.1/LICENSE.txt")
                    }
                }

                scm {
                    url.set("https://github.com/Team-EnderIO/EnderIO.git")
                }
            }
        }
    }
}
