plugins {
    id("maven-publish")
    id("net.neoforged.moddev")
    id("idea")
}

println("Release type: ${getReleaseType()}")

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}


// ============
// Utilities
// ============

fun getReleaseType(): String {
    // If we"re doing a proper build
    if (System.getenv("BUILD_VERSION") != null) {
        val versionString = System.getenv("BUILD_VERSION").lowercase()

        if (versionString.contains("alpha")) {
            return "alpha"
        } else if (versionString.contains("beta")) {
            return "beta"
        }

        return "release"
    }

    return "dev"
}
