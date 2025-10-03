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

val gametestImplementation by configurations.getting

configurations {
    runtimeClasspath.get().extendsFrom(create("localRuntime"))
}

dependencies {
    api(libs.regilite)
    api(project(":enderio"))
    accessTransformers(project(":enderio"))

    // Unit tests
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.neoforgeTestFramework)

    // Setup gametests
    gametestImplementation( libs.neoforgeTestFramework) {
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
        create("enderio_armory") {
            sourceSet(sourceSets.getByName("main"))
        }

        create("enderio_armory_tests") {
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
                    "--mod", "enderio_armory",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )

            loadedMods.set(listOf(mods.getByName("enderio_armory")))
        }

        // TODO: When we add gametests for modded conduits...
//        create("gameTestServer") {
//            type = "gameTestServer"
//
//            sourceSet = sourceSets.getByName("gametest")
//            loadedMods.set(listOf(mods.getByName("enderio_armory"), mods.getByName("enderio_armory_tests")))
//        }
    }

    unitTest {
        enable()
        testedMod = mods["enderio_armory"]
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
                "Specification-Title" to "Ender IO - Armory",
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

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = "com.enderio"
            artifactId = project.name
            version = "${project.version}"

            from(components["java"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO - Armory")
                description.set("Ender IO - Armory Addon")
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
