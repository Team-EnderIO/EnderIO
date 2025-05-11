import java.text.SimpleDateFormat
import java.util.*

val minecraftVersion: String by project
val neoForgeVersion: String by project

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

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")

    api(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))
    accessTransformersElements(project(":enderio-base"))

    // JEI
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge-api:$jeiVersion")

    api("dev.gigaherz.graph:GraphLib3:$graphlibVersion")
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
        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio_machines",
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }
    }

    mods {
        create("enderio_machines") {
            sourceSet(sourceSets["main"])
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO Machines",
                "Specification-Vendor" to "Team Ender IO",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Team Ender IO",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "MixinConfigs" to "enderio-machines.mixins.json"
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

            from(components["java"])
            artifact(tasks["apiJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("EnderIO Machines")
                description.set("The machines module of Ender IO")
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
