val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoForgeVersion: String by project
val neoForgeVersionRange: String by project
val loaderVersionRange: String by project

apply(from = rootProject.file("buildSrc/shared.gradle.kts"))

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

val regiliteVersion: String by project
val almostunifiedVersion: String by project

dependencies {
    // Regilite
    api("com.enderio:Regilite:${regiliteVersion}")

    // Almost Unified
    compileOnly("com.almostreliable.mods:almostunified-neoforge:1.21.1-${almostunifiedVersion}:api")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

neoForge {
    version = neoForgeVersion

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
