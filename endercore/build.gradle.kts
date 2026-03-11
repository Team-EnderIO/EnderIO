plugins {
    id("mod-common-conventions")
}

dependencies {
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.test {
    useJUnitPlatform()
}

val parchment_minecraft_version: String by project
val parchment_mappings_version: String by project


legacyForge {
    version = libs.versions.minecraft.get() + '-' + libs.versions.forge.get()

    parchment {
        mappingsVersion = parchment_mappings_version
        minecraftVersion = parchment_minecraft_version
    }

    addModdingDependenciesTo(sourceSets["test"])

    mods {
        create("endercore") {
            sourceSet(sourceSets["main"])
        }
    }

//    unitTest {
//        enable()
//        testedMod = mods["endercore"]
//    }
}

// Expand variables in mods.toml
var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
            "mod_version" to project.version,
            "minecraft_version_range" to libs.versions.minecraft.get(),
            "forge_version" to libs.versions.forge.get(),
            "loader_version_range" to "[47,)", // TODO
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

// Add results to source set and to IDE sync
sourceSets.main.get().resources.srcDir(generateModMetadata)
legacyForge.ideSyncTask(generateModMetadata)

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
