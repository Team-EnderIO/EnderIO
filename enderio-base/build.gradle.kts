import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("net.neoforged.moddev")
}

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

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")

    // EnderIO Base will bundle Regilite and EnderCore in production.
    jarJar("com.enderio:Regilite:$regiliteVersion")
    jarJar(project(":endercore"))

    // JEI
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge-api:$jeiVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

neoForge {
    version = neoForgeVersion

    accessTransformers {
        publish(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
    }

    runs {
        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio_base",
                    // TODO: Fix missing models...
                    //"--all",
                    "--server", "--client",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )
        }
    }

    mods {
        register("enderio_base") {
            sourceSet(sourceSets["main"])
        }
    }

    unitTest {
        enable()
        testedMod = mods["enderio_base"]
    }

    neoFormRuntime {
        // verbose = true
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO Base",
                "Specification-Vendor" to "Team Ender IO",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Team Ender IO",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "MixinConfigs" to "enderiobase.mixins.json"
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
                name.set("EnderIO Base")
                description.set("The base module of Ender IO")
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
