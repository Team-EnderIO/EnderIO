import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("mod-common-conventions")
}

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

configurations {
    create("gametestAnnotationProcessor") {
        extendsFrom(annotationProcessor.get())
    }
    create("gametestCompileOnly") {
        extendsFrom(compileOnly.get())
    }
    create("gametestImplementation") {
        extendsFrom(implementation.get())
    }
    create("gametestRuntimeOnly") {
        extendsFrom(runtimeOnly.get())
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }

    test {
    }

    create("gametest") {
        compileClasspath += sourceSets.main.get().output
    }
}

configurations {
    runtimeClasspath.get().extendsFrom(create("localRuntime"))
}

val gametestImplementation by configurations.getting

dependencies {
    api(libs.regilite)
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
    gametestImplementation(libs.neoforgeTestFramework) {
        isTransitive = false
    }
}

tasks.test {
    useJUnitPlatform()
}

neoForge {
    version = libs.versions.neoforge.get()

    addModdingDependenciesTo(sourceSets.getByName("gametest"))

    mods {
        create("enderio_modded_conduits") {
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

        create("data") {
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
            "loader_version_range" to "[4,)", // TODO
            "mekanism_version_range" to libs.versions.mekanismMod.get(),
            "ae2_version_range" to libs.versions.ae2.get(),
            "refinedstorage_version_range" to libs.versions.refinedStorage.get(),
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
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
