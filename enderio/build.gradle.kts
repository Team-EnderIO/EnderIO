import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("mod-common-conventions")
}

println("Building Ender IO version ${project.version}")

tasks.withType(JavaCompile::class) {
    options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
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

    create("gametest") {
        compileClasspath += sourceSets.main.get().output
        //runtimeClasspath += configurations.getByName("gametestLocalRuntime")
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

    testCompileOnly  {
        extendsFrom(compileOnly.get())
    }

    testRuntimeOnly  {
        // TODO: Mekanism breaks our unit tests...
        exclude(group = "mekanism", module = "Mekanism")
        extendsFrom(runtimeOnly.get())
    }

    testAnnotationProcessor  {
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
    val gametestImplementation by getting {
        extendsFrom(implementation.get())
    }
    val gametestCompileOnly by getting {
        extendsFrom(compileOnly.get())
    }
    val gametestRuntimeOnly by getting {
        extendsFrom(runtimeOnly.get())
    }
    val gametestAnnotationProcessor by getting {
        extendsFrom(annotationProcessor.get())
    }
}

val neoforgeVersionRange: String by project

// https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    val localRuntime by configurations.getting

    // EnderIO bundles EnderCore.
    api(project(":endercore"))
    jarJar(project(":endercore"))

    // Include built-in "addons"
    // 26.2-port: enderio-modded-conduits subproject removed — third-party mod interactions deferred
    // jarJar(project(":enderio-modded-conduits"))
    // localRuntime(project(":enderio-modded-conduits"))

    // Almost Unified
    // 26.2-port: third-party mod interaction commented out
//    compileOnly(variantOf(libs.almostUnified) {
//        classifier("api")
//    })

    // JEI
    compileOnly(libs.bundles.jeiApi)
     localRuntime(libs.bundles.jei)

    // CC: Tweaked
    compileOnly(libs.bundles.ccTweakedApi)
    localRuntime(libs.ccTweakedForge)

    // Jade for conduit addon
    compileOnly(libs.jade)
    localRuntime(libs.jade)

    //Athena ctm
    // 26.2-port: third-party mod interaction commented out
//    localRuntime(libs.athena)

    // AE2
//    compileOnly(variantOf(libs.ae2) {
//        classifier("api")
//    })
//
//    localRuntime(libs.ae2)

    // TODO: Re-add Enchantment descriptions if we add enchantments again

    // Mekanism
//    compileOnly(variantOf(libs.mekanism) {
//        classifier("api")
//    })

//    localRuntime(libs.mekanism)

    //Laserio
//    compileOnly(libs.laserio)
//    localRuntime(libs.laserio)

    // Graphlib
    api(libs.graphlib)
    jarJar(libs.graphlib)

    // FTB Ultimine Addon
    // TODO: Stop including FTB Library when it is transitively available via Ultimine
    // 26.2-port: third-party mod interaction commented out
//    compileOnly(libs.ftbUltimine)
//    compileOnly(libs.ftbLibrary)

    // TODO: Disabled until they fix Neo .21 compat
//    localRuntime(libs.ftbUltimine)
//    localRuntime(libs.ftbLibrary)

	// Curio compat
    compileOnly(libs.curios)
    localRuntime(libs.curios)

    // Sodium + Iris to test shader compatibility
    compileOnly(libs.iris)
    localRuntime(libs.sodium)
//    localRuntime(libs.iris)

    // Unit tests
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation("net.neoforged:testframework:${neoforgeVersionRange}")

    // Mocks
    testImplementation(libs.mockito)
    testImplementation(libs.mockitoJunit)
    mockitoAgent(libs.mockito) { isTransitive = false }

    // Setup gametests
    val gametestImplementation by configurations.getting
    gametestImplementation("net.neoforged:testframework:${neoforgeVersionRange}") {
        isTransitive = false
    }

    // Also allow running gametests in client+server but don't declare as a dependency
    localRuntime("net.neoforged:testframework:${neoforgeVersionRange}") {
        isTransitive = false
    }
}

val neoforgeVersion: String by project
neoForge {
    enable {
        version = neoforgeVersion
        isDisableRecompilation = System.getenv("CI") == "true"
    }

    accessTransformers {
        publish(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
    }

    addModdingDependenciesTo(sourceSets.getByName("datagen"))
    addModdingDependenciesTo(sourceSets.getByName("gametest"))

    val modEnderio by mods.creating {
        sourceSet(sourceSets.getByName("datagen"))
        sourceSet(sourceSets.getByName("main"))
    }

    val modEnderioGametests by mods.creating {
        sourceSet(sourceSets.getByName("gametest"))
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
        }

        // Client & Server runs contain default addons for ease.
        // Data + Game Test focus purely on the core mod.
        val client by creating {
            client()
            loadedMods = listOf(modEnderio)
        }

        val server by creating {
            server()
            loadedMods = listOf(modEnderio)

            gameDirectory = project.file("run/server")
        }

        val data by creating {
            clientData()
            loadedMods = listOf(modEnderio)

            programArguments.addAll(
                    "--mod", "enderio",
                    "--all",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }

        val gameTestServer by creating {
            type = "gameTestServer"
            loadedMods = listOf(modEnderio, modEnderioGametests)
            sourceSet = sourceSets["gametest"]
        }

        // Useful for debugging tests, but will include the gametest datapack so I separated the run config.
        val gameTestClient by creating {
            client()
            loadedMods = listOf(modEnderio, modEnderioGametests)
        }
    }

    unitTest {
        enable()
        testedMod = modEnderio
    }
}

// Expand variables in mods.toml
val minecraftVersionRange: String by project
var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
            "mod_version" to project.version,
            "minecraft_version_range" to minecraftVersionRange,
            "neoforge_version" to neoforgeVersionRange,
            "ftb_ultimine_version_range" to libs.versions.ftbUltimine.get(),
			"curios_version_range" to libs.versions.curios.get()
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

tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
    }
}

tasks.named<Jar>("jar") {
    // FIXME: Temporary - shipping datagen classes with build again for Endergy.
    from(sourceSets["datagen"].output)
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO",
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
                name.set("EnderIO")
                description.set("Ender IO")
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
