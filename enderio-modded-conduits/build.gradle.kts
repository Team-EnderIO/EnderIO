import com.hypherionmc.modpublisher.properties.ModLoader
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("net.neoforged.moddev")
    id("com.hypherionmc.modutils.modpublisher") version "2.+"
}

val minecraftVersion: String by project
val neoForgeVersion: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

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

val regiliteVersion: String by project
val almostunifiedVersion: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val cctMinecraftVersion: String by project
val cctVersion: String by project
val athenaVersion: String by project
val ae2Version: String by project
val refinedstorageVersion: String by project
val jadeFileId: String by project
val mekanismMinecraftVersion: String by project
val mekanismVersion: String by project
val curseforge_laserio_id: String by project
val curseforge_laserio_file: String by project
val graphlibVersion: String by project
val graphlibVersionRange: String by project
val ftbUltimineVersion: String by project

configurations {
    runtimeClasspath.get().extendsFrom(create("localRuntime"))
}

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")
    api(project(":enderio"))
    accessTransformers(project(":enderio"))

    // CC: Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge-api:$cctVersion")
    // TODO: Does not start on latest NeoForge
//    runtimeOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge:$cctVersion")

    // AE2
    compileOnly("appeng:appliedenergistics2:${ae2Version}:api")
    runtimeOnly("appeng:appliedenergistics2:${ae2Version}")

    // Mekanism
    compileOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}:api")
    runtimeOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}")

    // Refined Storage
    compileOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")

    //Laserio
    compileOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")
    runtimeOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")

    // FTB Ultimine Addon
    compileOnly("dev.ftb.mods:ftb-ultimine-neoforge:${ftbUltimineVersion}")

    // Unit tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("net.neoforged:testframework:${neoForgeVersion}")

    // Setup gametests
    add("gametestImplementation", "net.neoforged:testframework:$neoForgeVersion") {
        isTransitive = false
    }
}

tasks.test {
    useJUnitPlatform()
}

neoForge {
    version = neoForgeVersion

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

            // TODO: 1.22 - separate mod id.
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
