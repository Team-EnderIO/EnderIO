import java.net.URI

val libs = versionCatalogs.named("libs")

plugins {
    `java-library`
    `maven-publish`
    idea
}

apply(plugin = "net.neoforged.moddev")

group = "com.enderio"

val versionSeries: String by project
val buildVersion = System.getenv("BUILD_VERSION")
if (buildVersion != null) {
    version = buildVersion
} else {
    version = "$versionSeries-dev"
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

dependencies {
    compileOnly(libs.findLibrary("jetbrainsAnnotations").get())

    // Depend on ensure compiler plugin
    compileOnly(project(":ensure_plugin"))
    annotationProcessor(project(":ensure_plugin"))
    testCompileOnly(project(":ensure_plugin"))
    testAnnotationProcessor(project(":ensure_plugin"))

    testImplementation(libs.findLibrary("junitJupiter").get())
    testRuntimeOnly(libs.findLibrary("junitPlatformLauncher").get())
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Use ensure plugin
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xplugin:ContextEnsure")
    options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "400"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        if (System.getenv("RVR_MAVEN_USER") != null) {
            maven {
                name = "Rover656"
                url = URI("https://maven.rover656.dev/releases")
                credentials {
                    username = System.getenv("RVR_MAVEN_USER")
                    password = System.getenv("RVR_MAVEN_PASSWORD")
                }
            }
        }
    }
}
