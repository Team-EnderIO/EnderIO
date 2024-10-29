import java.text.SimpleDateFormat
import java.util.*

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

// TODO: Add ae2, rs2, mekanism etc. as optional deps in mods.toml

val regiliteVersion: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val graphlibVersion: String by project
val ae2Version: String by project
val mekanismMinecraftVersion: String by project
val mekanismVersion: String by project
val refinedstorageVersion: String by project
val curseforge_laserio_id: String by project
val curseforge_laserio_file: String by project

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")

    api(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))

    api(project(":enderio-conduits"))

    // AE2
    compileOnly("appeng:appliedenergistics2:${ae2Version}:api")

    // Mekanism
    compileOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}:api")

    // Refined Storage
    compileOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")

    //Laserio
    compileOnly("curse.maven:laserio-${curseforge_laserio_id}:${curseforge_laserio_file}")
}

neoForge {
    version = neoForgeVersion

    runs {
        create("data") {
            data()

            dependencies {
                runtimeOnly("appeng:appliedenergistics2:${ae2Version}")
                runtimeOnly("mekanism:Mekanism:${mekanismMinecraftVersion}-${mekanismVersion}")
                runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
            }

            programArguments.addAll(
                    "--mod", "enderio_conduits_modded",
                    "--all",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
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

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO Modded Conduits",
                "Specification-Vendor" to "Team Ender IO",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Team Ender IO",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
        ))
    }
}

tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")

    from(sourceSets["main"].output)
    from(sourceSets["main"].allJava)

    include("com/enderio/api/**")
    include("com/enderio/*/api/**")
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

            setOf("apiElements", "runtimeElements")
                    .flatMap { configName -> configurations[configName].hierarchy }
                    .forEach { configuration ->
                        configuration.dependencies.removeIf { dependency ->
                            dependency.name.contains("jei")
                        }
                    }

            from(components["java"])
            artifact(tasks["apiJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO Modded Conduits")
                description.set("The modded conduits support module of Ender IO")
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
