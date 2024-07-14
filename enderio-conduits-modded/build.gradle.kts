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
val ae2Version: String by project

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

    // AE2
    compileOnly("appeng:appliedenergistics2:${ae2Version}:api")
    runtimeOnly("appeng:appliedenergistics2:${ae2Version}")
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
