import com.hypherionmc.modpublisher.properties.ModLoader
import org.gradle.kotlin.dsl.invoke
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("mod-common-conventions")
    alias(libs.plugins.modpublisher)
}

// Because of the sourceset references to the addons
evaluationDependsOn(":enderio-armory")
evaluationDependsOn(":enderio-modded-conduits")

println("Building Ender IO version ${project.version}")

val localRuntime by configurations.creating
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }

    val datagen by creating {
        compileClasspath += sourceSets.main.get().output
    }

    val gametest by creating {
        compileClasspath += sourceSets.main.get().output
        //runtimeClasspath += configurations.getByName("gametestLocalRuntime")
    }
}

val testImplementation by configurations.getting
testImplementation.extendsFrom(configurations.implementation.get())
val testCompileOnly by configurations.getting
testCompileOnly.extendsFrom(configurations.compileOnly.get())
val testRuntimeOnly by configurations.getting {
    // TODO: Mekanism breaks our unit tests...
    exclude(group = "mekanism", module = "Mekanism")
    extendsFrom(configurations.runtimeOnly.get())
}
val testAnnotationProcessor by configurations.getting
testAnnotationProcessor.extendsFrom(configurations.annotationProcessor.get())

val datagenImplementation by configurations.getting
datagenImplementation.extendsFrom(configurations.implementation.get())
val datagenCompileOnly by configurations.getting
datagenCompileOnly.extendsFrom(configurations.compileOnly.get())
val datagenRuntimeOnly by configurations.getting
datagenRuntimeOnly.extendsFrom(configurations.runtimeOnly.get())
val datagenAnnotationProcessor by configurations.getting
datagenAnnotationProcessor.extendsFrom(configurations.annotationProcessor.get())

val gametestImplementation by configurations.getting
gametestImplementation.extendsFrom(configurations.implementation.get())
val gametestCompileOnly by configurations.getting
gametestCompileOnly.extendsFrom(configurations.compileOnly.get())
val gametestRuntimeOnly by configurations.getting
gametestRuntimeOnly.extendsFrom(configurations.runtimeOnly.get())
val gametestAnnotationProcessor by configurations.getting
gametestAnnotationProcessor.extendsFrom(configurations.annotationProcessor.get())

dependencies {
    // Include and bundle regilite
    api(libs.regilite)
    jarJar(libs.regilite)

    // EnderIO will bundle Regilite and EnderCore in production.
    api(project(":endercore"))
    jarJar(project(":endercore"))

    // Include built-in "addons"
    jarJar(project(":enderio-armory"))
    localRuntime(project(":enderio-armory"))
    jarJar(project(":enderio-modded-conduits"))
    localRuntime(project(":enderio-modded-conduits"))

    // Almost Unified
    compileOnly(variantOf(libs.almostUnified) {
        classifier("api")
    })

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
    localRuntime(libs.athena)

    // AE2
    compileOnly(variantOf(libs.ae2) {
        classifier("api")
    })

    localRuntime(libs.ae2)

    // TODO: Re-add Enchantment descriptions if we add enchantments again

    // Mekanism
    compileOnly(variantOf(libs.mekanism) {
        classifier("api")
    })

    localRuntime(libs.mekanism)

    // Refined Storage
    compileOnly(libs.refinedStorage)
    localRuntime(libs.refinedStorage)

    //Laserio
    compileOnly(libs.laserio)
    localRuntime(libs.laserio)

    // Graphlib
    api(libs.graphlib)
    jarJar(libs.graphlib)

    // FTB Ultimine Addon
    compileOnly(libs.ftbUltimine)
    localRuntime(libs.ftbUltimine)

    // Unit tests
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.neoforgeTestFramework)

    // Setup gametests
    gametestImplementation(libs.neoforgeTestFramework) {
        isTransitive = false
    }
}

neoForge {
    version = libs.versions.neoforge.get()

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
            data()
            loadedMods = listOf(modEnderio)

            programArguments.addAll(
                    "--mod", "enderio",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }

        val gameTestServer by creating {
            type = "gameTestServer"
            loadedMods = listOf(modEnderio, modEnderioGametests)
            sourceSet = sourceSets["gametest"]
        }
    }

    unitTest {
        enable()
        testedMod = modEnderio
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
            "ftb_ultimine_version_range" to libs.versions.ftbUltimine.get(),
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

val curseforge_projectId: String by project
val modrinth_projectId: String by project

if (getReleaseType() != null) {
    if (System.getenv("CHANGELOG") != null) {
        publisher {

            apiKeys {
                curseforge(System.getenv("CURSEFORGE_TOKEN"))
                modrinth(System.getenv("MODRINTH_TOKEN"))
            }

            debug.set(System.getenv("PUBLISH") != "true")

            curseID.set(curseforge_projectId)
            modrinthID.set(modrinth_projectId)

            versionType.set(getReleaseType())
            projectVersion.set("${project.version}")

            displayName.set("Ender IO - ${project.version}")
            changelog.set(System.getenv("CHANGELOG"))

            setGameVersions("1.21.1")
            setLoaders(ModLoader.NEOFORGE)

            curseEnvironment.set("both")
            artifact.set(tasks.jar)

            setJavaVersions(JavaVersion.VERSION_21)

            curseDepends {
                optional("jei", "athena", "applied-energistics-2", "mekanism", "cc-tweaked")
            }

            modrinthDepends {
                optional("jei", "athena-ctm", "ae2", "mekanism", "cc-tweaked")
            }
        }
    } else {
        println("Release disabled, no changelog found in environment");
    }
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
