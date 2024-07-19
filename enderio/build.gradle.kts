plugins {
    id("net.neoforged.moddev")
}

val minecraftVersion: String by project
val neoForgeVersion: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

println("Building Ender IO version ${project.version}")

val jeiVersion: String by project
val cctVersion: String by project
val athenaVersion: String by project
val ae2Version: String by project
val jadeFileId: String by project
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
    runtimeOnly("mezz.jei:jei-$minecraftVersion-common:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-neoforge:$jeiVersion")

    // CC: Tweaked
    // TODO: Does not start on latest NeoForge
//    runtimeOnly("cc.tweaked:cc-tweaked-$minecraftVersion-forge:$cctVersion")

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
    runtimeOnly("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}")
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
