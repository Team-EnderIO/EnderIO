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
    compileOnly(libs.findLibrary("jspecify").get())
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
        languageVersion = JavaLanguageVersion.of(25)
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

// TODO: Make this work outside of a git context.
val versionDetails: groovy.lang.Closure<VersionDetails> by extra
var details = versionDetails()

// TODO: Palantir doesn't let us filter for v prefixes on tags, this could cause issues if we tag anything else.
//       this plugin isn't perfect, but it'll do in the short term.
val tagVersion = Regex("""\d+(\.\d+)+(-\w+)?""").find(details.lastTag)?.value ?: "1.0.0"

// Without - metadata for dev builds
val strippedTagVersion = Regex("""\d+(\.\d+)+""").find(details.lastTag)?.value ?: "1.0.0"

if (details.commitDistance == 0 && details.isCleanTag) {
    version = tagVersion
} else if (details.branchName != null) {
    version = "$strippedTagVersion.${details.commitDistance}-${details.branchName.replace("/", "-")}+${details.gitHash}"
} else {
    version = "$strippedTagVersion.${details.commitDistance}-dev+${details.gitHash}"
}
