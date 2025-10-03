import com.palantir.gradle.gitversion.VersionDetails
import java.net.URI

val libs = versionCatalogs.named("libs")

plugins {
    `java-library`
    `maven-publish`
    idea
    id("com.palantir.git-version")
}

apply(plugin = "net.neoforged.moddev")

group = "com.enderio"

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

//   * enderio-7.0.1-alpha.jar      :: release version 7.0.1-alpha (discovered by git tag)
//   * enderio-7.0.1.349-nightly    :: nightly build no. 349, based after 7.0.1.
//   * enderio-7.0-dev+c91c8ee6e    :: dev (local) build for commit c91c8ee6e for version set 7.0.
if (System.getenv("BUILD_VERSION") != null) {
    var buildVersion = System.getenv("BUILD_VERSION")
    if (buildVersion.startsWith("v")) {
        buildVersion = buildVersion.substring(1)
    }

    version = buildVersion
} else {
    val versionSeries: String by project

    val versionDetails: groovy.lang.Closure<VersionDetails> by extra
    var details = versionDetails()

    // If this is not a release, we're going to get the last tag, add the ci build number, then append -dev+<commit_hash>

    // Extract the numeric component of the last version.
    var versionRegex = Regex("""\d+(\.\d+)+""")
    var currentVersion = versionRegex.find(details.lastTag)?.value
    if (currentVersion == null) {
        // Fallback to version series if we're unable to discover the previous version.
        currentVersion = "$versionSeries.0"
    }

    if (System.getenv("BUILD_NUMBER") != null) {
        val buildNumber = System.getenv("BUILD_NUMBER")
        version = "$currentVersion.$buildNumber-nightly+${details.gitHash}"
    } else {
        version = "$versionSeries-dev+${details.gitHash}"
    }
}

println("Version: $version")
