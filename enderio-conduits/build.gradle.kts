plugins {
    id("net.neoforged.moddev")
}

val minecraftVersion: String by project
val minecraft_version_range: String by project
val neoForgeVersion: String by project
val neo_version_range: String by project
val loader_version_range: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}

val regiliteVersion: String by project
val jeiVersion: String by project
val graphlibVersion: String by project
val graphlibVersionRange: String by project
val cctVersion: String by project

dependencies {
    implementation("com.enderio:Regilite:$regiliteVersion")

    implementation(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))

    compileOnly(project(":ensure_plugin"))

    implementation(project(":endercore"))

    // JEI
    compileOnly("mezz.jei:jei-$minecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-common:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-neoforge:$jeiVersion")

    //CC-Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$minecraftVersion-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$minecraftVersion-forge-api:$cctVersion")
    // TODO: Does not start on latest NeoForge
//    runtimeOnly("cc.tweaked:cc-tweaked-$minecraftVersion-forge:$cctVersion")

    implementation("dev.gigaherz.graph:GraphLib3:$graphlibVersion")
    jarJar("dev.gigaherz.graph:GraphLib3:$graphlibVersion") {
        version {
            strictly(graphlibVersionRange)
            prefer(graphlibVersion)
        }
    }
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

        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio_conduits",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
                    "--existing", file("../enderio-base/src/main/resources").absolutePath,
                    "--existing", file("../enderio-base/src/generated/resources").absolutePath,
            )
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

        create("enderio_conduits") {
            sourceSet(sourceSets.getByName("main"))
        }
    }
}
