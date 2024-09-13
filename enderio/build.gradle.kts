import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("net.neoforged.moddev")
    id("com.hypherionmc.modutils.modpublisher") version "2.+"
}

val minecraftVersion: String by project
val neoForgeVersion: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

println("Building Ender IO version ${project.version}")

val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val cctMinecraftVersion: String by project
val cctVersion: String by project
val athenaVersion: String by project
val ae2Version: String by project
val jadeFileId: String by project
val mekanismMinecraftVersion: String by project
val mekanismVersion: String by project

dependencies {
    // Include modules
    jarJar(project(":enderio-base"))
    jarJar(project(":enderio-machines"))
    jarJar(project(":enderio-conduits"))
    jarJar(project(":enderio-conduits-modded"))
    jarJar(project(":enderio-armory"))
    implementation(project(":enderio-base"))
    implementation(project(":enderio-machines"))
    implementation(project(":enderio-conduits"))
    implementation(project(":enderio-conduits-modded"))
    implementation(project(":enderio-armory"))

    // JEI
    runtimeOnly("mezz.jei:jei-$jeiMinecraftVersion-common:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge:$jeiVersion")

    // CC: Tweaked
    // TODO: Does not start on latest NeoForge
//    runtimeOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge:$cctVersion")

    //Athena ctm
    runtimeOnly("maven.modrinth:athena-ctm:${athenaVersion}")

    // AE2
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

    // Jade
    runtimeOnly("curse.maven:jade-324717:${jadeFileId}")

    //fluxnetworks
    ////runtimeOnly("curse.maven:fluxnetworks-248020:4651164")

    // Patchouli
    //runtimeOnly("vazkii.patchouli:Patchouli:${patchouli_version}")

    // Mekanism
    runtimeOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}")
}

neoForge {
    version = neoForgeVersion

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
        }

        create("client") {
            client()
        }

        create("server") {
            server()
        }
    }

    mods {
        create("endercore") {
            dependency(project(":endercore"))
        }

        create("enderio_base") {
            sourceSet(project(":enderio-base").sourceSets["main"])
        }

        create("enderio_machines") {
            sourceSet(project(":enderio-machines").sourceSets["main"])
        }

        create("enderio_conduits") {
            sourceSet(project(":enderio-conduits").sourceSets["main"])
        }

        create("enderio_conduits_modded") {
            sourceSet(project(":enderio-conduits-modded").sourceSets["main"])
        }

        create("enderio_armory") {
            sourceSet(project(":enderio-armory").sourceSets["main"])
        }
    }
}

// Collect all API packages from all modules.
tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")

    from(project(":enderio-armory").sourceSets["main"].output)
    from(project(":enderio-armory").sourceSets["main"].allJava)
    from(project(":enderio-base").sourceSets["main"].output)
    from(project(":enderio-base").sourceSets["main"].allJava)
    from(project(":enderio-conduits").sourceSets["main"].output)
    from(project(":enderio-conduits").sourceSets["main"].allJava)
    from(project(":enderio-machines").sourceSets["main"].output)
    from(project(":enderio-machines").sourceSets["main"].allJava)

    include("com/enderio/api/**")
    include("com/enderio/*/api/**")
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")

    from(project(":enderio-armory").sourceSets["main"].allJava)
    from(project(":enderio-base").sourceSets["main"].allJava)
    from(project(":enderio-conduits").sourceSets["main"].allJava)
    from(project(":enderio-machines").sourceSets["main"].allJava)
}

tasks.build {
    dependsOn(tasks["apiJar"])
    dependsOn(tasks["sourcesJar"])
}

publishing {
    publications {
        create<MavenPublication>("enderio") {
            groupId = "com.enderio"
            // TODO: Do we care about specifying MC version now that we're only releasing one major version per MC version?
            //       Only real benefit is this being clear in maven directly.
            artifactId = "enderio-${minecraftVersion}"
            version = "${project.version}"

            artifact(tasks["jar"])
            artifact(tasks["apiJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO")
                description.set("The core modules of Ender IO")
                url.set("https://github.com/Team-EnderIO/EnderIO")

                licenses {
                    license {
                        name.set("Unlicense")
                        url.set("https://github.com/Team-EnderIO/EnderIO/blob/dev/1.21/LICENSE.txt")
                    }
                }

                scm {
                    url.set("https://github.com/Team-EnderIO/EnderIO.git")
                }
            }
        }
    }
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
            projectVersion.set("$project.version")

            displayName.set("Ender IO - $project.version")
            changelog.set(System.getenv("CHANGELOG"))

            setGameVersions("1.21", "1.21.1")
            setLoaders(ModLoader.NEOFORGE)

            curseEnvironment.set("both")
            artifact.set(tasks.jar)

            setJavaVersions(JavaVersion.VERSION_21)

            curseDepends {
                optional("jei", /*"patchouli",*/ "athena", "applied-energistics-2", "mekanism", "cc-tweaked")
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
