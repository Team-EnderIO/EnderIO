import com.hypherionmc.modpublisher.properties.ModLoader
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("net.neoforged.moddev")
    id("com.hypherionmc.modutils.modpublisher") version "2.+"
}

// Because of the sourceset reference
evaluationDependsOn(":enderio-armory")
evaluationDependsOn(":enderio-modded-conduits")

val minecraftVersion: String by project
val neoForgeVersion: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

println("Building Ender IO version ${project.version}")

configurations {
    create("datagenAnnotationProcessor") {
        extendsFrom(annotationProcessor.get())
    }
    create("datagenCompileOnly") {
        extendsFrom(compileOnly.get())
    }
    create("datagenImplementation") {
        extendsFrom(implementation.get())
    }
    create("datagenRuntimeOnly") {
        extendsFrom(runtimeOnly.get())
    }

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

    testRuntimeOnly {
        // TODO: Mekanism breaks our unit tests...
        exclude(group = "mekanism", module = "Mekanism")
    }
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

    // EnderIO will bundle Regilite and EnderCore in production.
    jarJar("com.enderio:Regilite:$regiliteVersion")
    jarJar(project(":endercore"))

    // Include built-in "addons"
    jarJar(project(":enderio-armory"))
    add("localRuntime", project(":enderio-armory"))
    jarJar(project(":enderio-modded-conduits"))
    add("localRuntime", project(":enderio-modded-conduits"))

    // Almost Unified
    compileOnly("com.almostreliable.mods:almostunified-neoforge:1.21.1-${almostunifiedVersion}:api")

    // JEI
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge-api:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$jeiMinecraftVersion-common:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge:$jeiVersion")

    // CC: Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge-api:$cctVersion")
    // TODO: Does not start on latest NeoForge
//    runtimeOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge:$cctVersion")

    // Jade for conduit addon
    compileOnly("curse.maven:jade-324717:${jadeFileId}")
    runtimeOnly("curse.maven:jade-324717:${jadeFileId}")

    //Athena ctm
    runtimeOnly("maven.modrinth:athena-ctm:${athenaVersion}")

    // AE2
    compileOnly("appeng:appliedenergistics2:${ae2Version}:api")
    runtimeOnly("appeng:appliedenergistics2:${ae2Version}")

    // Enchantment descriptions
    //runtimeOnly("net.darkhax.bookshelf:Bookshelf-NeoForge-${minecraft_version}:${bookshelf_version}")
    //runtimeOnly("net.darkhax.enchdesc:EnchantmentDescriptions-NeoForge-${minecraft_version}:${ench_desc_version}")

    // The One Probe https://github.com/McJtyMods/TheOneProbe/issues/548
    //compileOnly("mcjty.theoneprobe:theoneprobe:${top_version}:api") {
    //    transitive = false
    //}
    //runtimeOnly("mcjty.theoneprobe:theoneprobe:${top_version}") {
    //    transitive = false
    //}

    //fluxnetworks
    ////runtimeOnly("curse.maven:fluxnetworks-248020:4651164")

    // Patchouli
    //runtimeOnly("vazkii.patchouli:Patchouli:${patchouli_version}")

    // Mekanism
    compileOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}:api")
    runtimeOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}")

    // Refined Storage
    compileOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")

    //Laserio
    compileOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")
    runtimeOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")

    // Graphlib
    api("dev.gigaherz.graph:GraphLib3:$graphlibVersion")
    jarJar("dev.gigaherz.graph:GraphLib3:$graphlibVersion") {
        version {
            strictly(graphlibVersionRange)
            prefer(graphlibVersion)
        }
    }

    // FTB Ultimine Addon
    compileOnly("dev.ftb.mods:ftb-ultimine-neoforge:${ftbUltimineVersion}")
    runtimeOnly("dev.ftb.mods:ftb-ultimine-neoforge:${ftbUltimineVersion}")

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

    accessTransformers {
        publish(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
    }

    addModdingDependenciesTo(sourceSets.getByName("datagen"))
    addModdingDependenciesTo(sourceSets.getByName("gametest"))

    mods {
        create("enderio") {
            sourceSet(sourceSets.getByName("datagen"))
            sourceSet(sourceSets.getByName("main"))
        }

        create("enderio_tests") {
            sourceSet(sourceSets.getByName("gametest"))
        }

        create("enderio_armory") {
            sourceSet(project(":enderio-armory").sourceSets.getByName("main"))
        }

        create("enderio_modded_conduits") {
            sourceSet(project(":enderio-modded-conduits").sourceSets.getByName("main"))
        }
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
        }

        // Client & Server runs contain default addons for ease.
        // Data + Game Test focus purely on the core mod.
        create("client") {
            client()

            loadedMods.set(listOf(
                    mods.getByName("enderio"),
                    mods.getByName("enderio_armory"),
                    mods.getByName("enderio_modded_conduits")
            ))
        }

        create("server") {
            server()
            gameDirectory = project.file("run/server")

            loadedMods.set(listOf(
                    mods.getByName("enderio"),
                    mods.getByName("enderio_armory"),
                    mods.getByName("enderio_modded_conduits")
            ))
        }

        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )

            loadedMods.set(listOf(mods.getByName("enderio")))
        }

        create("gameTestServer") {
            type = "gameTestServer"

            sourceSet = sourceSets.getByName("gametest")
            loadedMods.set(listOf(mods.getByName("enderio"), mods.getByName("enderio_tests")))
        }
    }

    unitTest {
        enable()
        testedMod = mods["enderio"]
    }
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
                optional("jei", /*"patchouli",*/ "stitch", "applied-energistics-2", "mekanism", "cc-tweaked")
            }

            modrinthDepends {
                optional("jei", "stitch", "ae2", "mekanism", "cc-tweaked")
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
