plugins {
    id("mod-common-conventions")
}

val neoforgeVersionRange: String by project
dependencies {
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation("net.neoforged:testframework:${neoforgeVersionRange}")
}

tasks.test {
    useJUnitPlatform()
}

val neoforgeVersion: String by project
neoForge {
    enable {
        version = neoforgeVersion
        isDisableRecompilation = System.getenv("CI") == "true"
    }

    mods {
        create("endercore") {
            sourceSet(sourceSets["main"])
        }
    }

    unitTest {
        enable()
        testedMod = mods["endercore"]
    }
}

// Expand variables in mods.toml
val minecraftVersionRange: String by project
var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
            "mod_version" to project.version,
            "minecraft_version_range" to minecraftVersionRange,
            "neoforge_version" to neoforgeVersion,
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

// Add results to source set and to IDE sync
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

publishing {
    publications {
        create<MavenPublication>("endercore") {
            groupId = "com.enderio"
            artifactId = "endercore"
            version = "${project.version}"

            from(components["java"])

            pom {
                name.set("EnderCore")
                description.set("Ender Core is the library mod backing Ender IO")
                url.set("https://github.com/Team-EnderIO/EnderCore")

                licenses {
                    license {
                        name.set("Unlicense")
                        url.set("https://github.com/Team-EnderIO/EnderCore/blob/dev/1.21/LICENSE.txt")
                    }
                }

                scm {
                    url.set("https://github.com/Team-EnderIO/EnderCore.git")
                }
            }
        }
    }
}
