plugins {
    id("net.neoforged.moddev")
}

val minecraftVersion: String by project
val minecraft_version_range: String by project
val neoForgeVersion: String by project
val neo_version_range: String by project
val loader_version_range: String by project
val curseforge_laserio_id: String by project
val curseforge_laserio_file: String by project

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

// TODO: Add ae2, rs2, mekanism etc. as optional deps in mods.toml

val regiliteVersion: String by project
val jeiVersion: String by project
val graphlibVersion: String by project
val ae2Version: String by project
val mekanismVersion: String by project
val refinedstorageVersion: String by project


dependencies {
    implementation("com.enderio:Regilite:$regiliteVersion")

    implementation(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))

    implementation(project(":enderio-conduits"))

    compileOnly(project(":ensure_plugin"))

    implementation(project(":endercore"))

    // JEI
    runtimeOnly("mezz.jei:jei-$minecraftVersion-common:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-neoforge:$jeiVersion")

    implementation("dev.gigaherz.graph:GraphLib3:$graphlibVersion")

    // AE2
    compileOnly("appeng:appliedenergistics2:${ae2Version}:api")
    runtimeOnly("appeng:appliedenergistics2:${ae2Version}")

    // Mekanism
    compileOnly("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}:api")
    runtimeOnly("mekanism:Mekanism:${minecraftVersion}-${mekanismVersion}")

    // Refined Storage
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")

    //Laserio
    compileOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")
    runtimeOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")
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
                    "--mod", "enderio_conduits_modded",
                    "--all",
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
            sourceSet(project(":enderio-conduits").sourceSets["main"])
        }

        create("enderio_conduits_modded") {
            sourceSet(sourceSets.getByName("main"))
        }
    }
}
