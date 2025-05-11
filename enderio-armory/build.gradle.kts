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
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val graphlibVersion: String by project
val graphlibVersionRange: String by project
val cctMinecraftVersion: String by project
val cctVersion: String by project

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")

    api(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))

    api(project(":enderio-machines"))
    accessTransformers(project(":enderio-machines"))
    accessTransformersElements(project(":enderio-machines"))

    // JEI
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge-api:$jeiVersion")

    //CC-Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge-api:$cctVersion")
}

neoForge {
    version = neoForgeVersion

    runs {
        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio_armory",
                    "--all",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }
    }

    mods {
        register("enderio_armory") {
            sourceSet(sourceSets["main"])
        }
    }
}
