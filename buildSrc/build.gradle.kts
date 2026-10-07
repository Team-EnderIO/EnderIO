plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.palantir.git-version:com.palantir.git-version.gradle.plugin:4.3.0")
}
