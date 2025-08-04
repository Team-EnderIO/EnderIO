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

configurations {
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
    create("gametestLocalRuntime") {
        extendsFrom(runtimeOnly.get())
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
    create("gametest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += configurations.getByName("gametestLocalRuntime")
    }
}

val regiliteVersion: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val cctMinecraftVersion: String by project
val cctVersion: String by project
val jadeFileId: String by project
val ftbUltimineVersion: String by project

configurations {
    runtimeClasspath.get().extendsFrom(create("localRuntime"))
}

dependencies {
    api("com.enderio:Regilite:$regiliteVersion")

    api(project(":enderio-base"))
    accessTransformers(project(":enderio-base"))

    // JEI
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$jeiMinecraftVersion-neoforge-api:$jeiVersion")

    //CC-Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$cctMinecraftVersion-forge-api:$cctVersion")

    // Jade for addon
    compileOnly("curse.maven:jade-324717:${jadeFileId}")

    // FTB Ultimine Addon
    compileOnly("dev.ftb.mods:ftb-ultimine-neoforge:${ftbUltimineVersion}")

    // For painting recipe.
    // TODO: This isn't great.
    compileOnly(project(":enderio-machines"))
    add("localRuntime", project(":enderio-machines"))

    // Setup gametests
    add("gametestImplementation", "net.neoforged:testframework:$neoForgeVersion") {
        isTransitive = false
    }
    add("gametestRuntimeOnly", project(":enderio-machines"))
}

neoForge {
    version = neoForgeVersion

    addModdingDependenciesTo(sourceSets.getByName("gametest"))

    mods {
        create("enderio_conduits") {
            sourceSet(sourceSets.getByName("main"))
        }

        create("enderio_conduits_tests") {
            sourceSet(sourceSets.getByName("gametest"))
        }
    }

    runs {
        create("data") {
            data()

            programArguments.addAll(
                    "--mod", "enderio_conduits",
                    //"--all",
                    "--client", "--server",
                    "--output", file("src/generated/resources").absolutePath,
                    "--existing", file("src/main/resources").absolutePath,
            )

            loadedMods.set(listOf(mods.getByName("enderio_conduits")))
        }

        create("gameTestServer") {
            type = "gameTestServer"

            sourceSet = sourceSets.getByName("gametest")
            loadedMods.set(listOf(mods.getByName("enderio_conduits"), mods.getByName("enderio_conduits_tests")))
        }
    }
}

// Gross hack for gametests for now.
val minecraftVersionRange: String by project
val neoForgeVersionRange: String by project
val loaderVersionRange: String by project
val replaceProperties = mapOf(
        "mod_version" to project.version,
        "mcversion" to minecraftVersionRange,
        "neo_version" to neoForgeVersionRange,
        "loader_version_range" to loaderVersionRange
)

tasks.withType<ProcessResources>().configureEach {
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
        expand(mutableMapOf("project" to project))
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to "Ender IO Conduits",
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
                name.set("EnderIO Conduits")
                description.set("The conduits module of Ender IO")
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

