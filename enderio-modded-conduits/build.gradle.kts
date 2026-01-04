import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("mod-common-conventions")
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

    test {
    }

    create("gametest") {
        compileClasspath += sourceSets.main.get().output
    }
}

configurations {
    val localRuntime by creating

    runtimeClasspath {
        extendsFrom(localRuntime)
    }

    val gametestAnnotationProcessor by getting {
        extendsFrom(annotationProcessor.get())
    }
    val gametestCompileOnly by getting {
        extendsFrom(compileOnly.get())
    }
    val gametestImplementation by getting {
        extendsFrom(implementation.get())
    }
    val gametestRuntimeOnly by getting {
        extendsFrom(runtimeOnly.get())
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
    api(project(":enderio"))
    accessTransformers(project(":enderio"))

    // AE2
    compileOnly(variantOf(libs.ae2) {
        classifier("api")
    })

    runtimeOnly(libs.ae2)

    // Mekanism
    compileOnly(variantOf(libs.mekanism) {
        classifier("api")
    })

    runtimeOnly(libs.mekanism)

    // Refined Storage
    compileOnly(libs.refinedStorage)
    runtimeOnly(libs.refinedStorage)

    //Laserio
    compileOnly(libs.laserio)
    runtimeOnly(libs.laserio)

    // Unit tests
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.neoforgeTestFramework)

    // Setup gametests
    val gametestImplementation by configurations.getting
    gametestImplementation(libs.neoforgeTestFramework) {
        isTransitive = false
    }
}

neoForge {
    enable {
        version = libs.versions.neoforge.get()
        isDisableRecompilation = System.getenv("CI") == "true"
    }

    addModdingDependenciesTo(sourceSets.getByName("datagen"))
    addModdingDependenciesTo(sourceSets.getByName("gametest"))

    mods {
        create("enderio_modded_conduits") {
            sourceSet(sourceSets.getByName("datagen"))
            sourceSet(sourceSets.getByName("main"))
        }

        create("enderio_modded_conduits_tests") {
            sourceSet(sourceSets.getByName("gametest"))
        }
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
        }

        val data by creating {
            data()

            programArguments.addAll(
                    "--mod", "enderio_modded_conduits",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )

            loadedMods.set(listOf(mods.getByName("enderio_modded_conduits")))
        }

        // TODO: When we add gametests for modded conduits...
//        create("gameTestServer") {
//            type = "gameTestServer"
//
//            sourceSet = sourceSets.getByName("gametest")
//            loadedMods.set(listOf(mods.getByName("enderio_modded_conduits"), mods.getByName("enderio_modded_conduits_tests")))
//        }
    }

    unitTest {
        enable()
        testedMod = mods["enderio_modded_conduits"]
    }
}

// Expand variables in mods.toml
var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
            "mod_version" to project.version,
            "minecraft_version_range" to libs.versions.minecraft.get(),
            "neoforge_version" to libs.versions.neoforge.get(),
            "mekanism_version_range" to libs.versions.mekanismMod.get(),
            "ae2_version_range" to libs.versions.ae2.get(),
            "refinedstorage_version_range" to libs.versions.refinedStorage.get(),
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
                "Specification-Title" to "Ender IO - Modded Conduits",
                "Specification-Vendor" to "Team Ender IO",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Team Ender IO",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        ))
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allJava)
}

tasks.build {
    dependsOn(tasks["sourcesJar"])
}

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
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO - Modded Conduits")
                description.set("Ender IO - Modded Conduits Addon")
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
