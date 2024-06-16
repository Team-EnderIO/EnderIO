import org.gradle.plugins.ide.idea.model.IdeaModule
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.function.Consumer

plugins {
    id("eclipse")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
    id("net.neoforged.gradle.userdev") version "7.0.143"
    id("com.hypherionmc.modutils.modpublisher") version "2.+"
}

val mod_id: String by project
val minecraft_version: String by project
val minecraft_version_range: String by project
val neo_version: String by project
val neo_version_range: String by project
val loader_version_range: String by project

val curseforge_projectId: String by project
val modrinth_projectId: String by project
val modrinth_dep_jei: String by project
val modrinth_dep_athena: String by project
val modrinth_dep_ae2: String by project
val cctVersion: String by project

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val mod_version = getVersionString()

version = "${minecraft_version}-${mod_version}"
group = "com.enderio"

base {
    archivesName.set("EnderIO")
}

println("Building Ender IO version $version")
println("Release type: ${getReleaseType()}")

// Mojang ships Java 21 to end users in 1.20.5+, so your mod should target Java 21.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

// List of all subsets. This is used for dividing the mod into logical components.
// TODO: 1.19: Tidy the divisions and what goes where.
val subsets = listOf<String>(
        "conduits",
        "machines",
        "armory"
)

sourceSets {
    create("ensure_plugin")
    create("api") {
        compileClasspath += sourceSets.getByName("ensure_plugin").output
    }
    create("core") {
        compileClasspath += sourceSets.getByName("api").output
        compileClasspath += sourceSets.getByName("ensure_plugin").output
    }
    main {
        compileClasspath += sourceSets.getByName("api").output
        compileClasspath += sourceSets.getByName("core").output
        compileClasspath += sourceSets.getByName("ensure_plugin").output
        //ext.refMap = "mixins.enderio.refmap.json"
        resources.srcDir("src/generated/resources")
    }
}

// Configure the API source set.
configurations {
    named("apiImplementation") {
        extendsFrom(implementation.get())
    }

    named("apiCompileOnly") {
        extendsFrom(compileOnly.get())
    }

    named("apiRuntimeOnly") {
        extendsFrom(runtimeOnly.get())
    }

    named("coreImplementation") {
        extendsFrom(implementation.get())
    }

    named("coreCompileOnly") {
        extendsFrom(compileOnly.get())
    }

    named("coreRuntimeOnly") {
        extendsFrom(runtimeOnly.get())
    }
}

// Add all subset source sets.
for (set in subsets) {
    setupSourceSet(set)
}

minecraft.accessTransformers {
    file(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))
}

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")

        modSource(sourceSets.getByName("main"))
        modSource(sourceSets.getByName("core"))
        modSource(sourceSets.getByName("api"))

        modSources

        for (set in subsets) {
            modSource(sourceSets.getByName(set))
        }
    }

    create("client") {
        workingDirectory(project.file("run"))

        // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
        systemProperty("forge.enabledGameTestNamespaces", "enderio")
    }

    create("server") {
        workingDirectory(project.file("run-server"))

        systemProperty("forge.enabledGameTestNamespaces", "enderio")
    }

    create("data") {
        workingDirectory(project.file("run"))

        programArguments.addAll(
                "--mod", "enderio",
                "--server", "--client",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
                "--existing", file("src/machines/resources/").absolutePath,
                "--existing", file("src/conduits/resources/").absolutePath,
                "--existing", file("src/armory/resources/").absolutePath)
    }
}

val replaceProperties = mapOf(
        "version" to mod_version,
        "mcversion" to minecraft_version_range,
        "neo_version" to neo_version_range,
        "loader_version_range" to loader_version_range
)

tasks.withType<ProcessResources>().configureEach {
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
        expand(mutableMapOf("project" to project))
    }
}

// pupnewfster helpers for exclusive repos
fun RepositoryHandler.exclusiveRepo(url: String, vararg groups: String) {
    exclusiveRepo(url) { filter ->
        groups.forEach { group ->
            filter.includeGroup(group)
        }
    }
}

fun RepositoryHandler.exclusiveRepo(url: String, filterSetup: (InclusiveRepositoryContentDescriptor) -> Unit) {
    exclusiveContent {
        forRepository {
            maven {
                setUrl(url)
            }
        }
        filter(filterSetup)
    }
}

repositories {
    exclusiveRepo("https://modmaven.dev/", "mezz.jei", "mcjty.theoneprobe", "appeng", "mekanism")
    exclusiveRepo("https://cursemaven.com", "curse.maven")
    exclusiveRepo("https://maven.blamejared.com", "vazkii.patchouli", "net.darkhax.bookshelf", "net.darkhax.enchdesc", "com.almostreliable.mods")
    exclusiveRepo("https://dogforce-games.com/maven", "dev.gigaherz.graph")
    exclusiveRepo("https://api.modrinth.com/maven", "maven.modrinth")
    exclusiveRepo("https://maven.parchmentmc.org/", "org.parchmentmc.data")
    exclusiveRepo("https://maven.rover656.dev/releases", "com.enderio")
    exclusiveRepo("https://squiddev.cc/maven/", "cc.tweaked")

    mavenLocal()
}

jarJar.enable()

val regilite_version: String by project
val graphlib_version: String by project
val graphlib_version_range: String by project
val jei_version: String by project
val athena_version: String by project
val ae2_version: String by project
val bookshelf_version: String by project
val ench_desc_version: String by project
val jade_cf_id: String by project
val mekanism_version: String by project
val patchouli_version: String by project

dependencies {
    // NeoForge
    implementation("net.neoforged:neoforge:$neo_version")

    //implementation("blank:Xycraft+Core:0.5.2")
    //runtimeOnly("blank:Xycraft+Override:0.5.2")
    //runtimeOnly("blank:Xycraft+World:0.5.2")

    // Regilite
    implementation("com.enderio:Regilite:${regilite_version}")
    jarJar("com.enderio:Regilite:[${regilite_version}]")

    // GraphLib
    implementation("dev.gigaherz.graph:GraphLib3:${graphlib_version}")
    jarJar("dev.gigaherz.graph:GraphLib3:${graphlib_version}") {
        jarJar.ranged(this, graphlib_version_range)
    }

    // Mixin annotations
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    // JEI
    compileOnly("mezz.jei:jei-${minecraft_version}-common-api:${jei_version}")
    compileOnly("mezz.jei:jei-${minecraft_version}-neoforge-api:${jei_version}")
    runtimeOnly("mezz.jei:jei-${minecraft_version}-common:${jei_version}")
    runtimeOnly("mezz.jei:jei-${minecraft_version}-neoforge:${jei_version}")

    //RFTOOLS
    //runtimeOnly("maven.modrinth:rftools-power:f430rHkA")
    //runtimeOnly("maven.modrinth:mcjtylib:3LlgyvSh")
    //runtimeOnly("maven.modrinth:rftools-base:Uu1IkVMH")
    //runtimeOnly("maven.modrinth:spark:Yp6s4wsw")

    //Athena ctm
    //runtimeOnly("maven.modrinth:athena-ctm:${athena_version}")

    // AE2
    compileOnly("appeng:appliedenergistics2-neoforge:${ae2_version}:api")
    runtimeOnly("appeng:appliedenergistics2-neoforge:${ae2_version}")

    // Enchantment descriptions
    //runtimeOnly("net.darkhax.bookshelf:Bookshelf-NeoForge-${minecraft_version}:${bookshelf_version}")
    //runtimeOnly("net.darkhax.enchdesc:EnchantmentDescriptions-NeoForge-${minecraft_version}:${ench_desc_version}")

    // The One Probe https://github.com/McJtyMods/TheOneProbe/issues/548
    //compileOnly("mcjty.theoneprobe:theoneprobe:${top_version}:api") {
    //    transitive = false
    //}
    //runtimeOnly("mcjty.theoneprobe:theoneprobe:${top_version}") {
    //    transitive = false
    //}

    // Jade
    //runtimeOnly("curse.maven:jade-324717:${jade_cf_id}")

    //fluxnetworks
    ////runtimeOnly("curse.maven:fluxnetworks-248020:4651164")

    //Flywheel
    ////compileOnly("com.jozufozu.flywheel:flywheel-forge-1.20.1:0.6.9-5") // REMOVE When crash is fixed

    // Almost Unified
    ////compileOnly("com.almostreliable.mods:almostunified-forge:${minecraft_version}-${almostunified_version}")

    // Patchouli
    //compileOnly("vazkii.patchouli:Patchouli:${patchouli_version}:api")
    //runtimeOnly("vazkii.patchouli:Patchouli:${patchouli_version}")

    // Mekanism
    //compileOnly("mekanism:Mekanism:${minecraft_version}-${mekanism_version}:api")
    //runtimeOnly("mekanism:Mekanism:${minecraft_version}-${mekanism_version}")

    //CC-Tweaked
    compileOnly("cc.tweaked:cc-tweaked-$minecraft_version-core-api:$cctVersion")
    compileOnly("cc.tweaked:cc-tweaked-$minecraft_version-forge-api:$cctVersion")
    runtimeOnly("cc.tweaked:cc-tweaked-$minecraft_version-forge:$cctVersion")

    // Jetbrains annotations
    compileOnly("org.jetbrains:annotations:23.0.0")
}

// Example for how to get properties into the manifest for reading at runtime.
tasks.withType<Jar> {
    archiveClassifier.set("partial")

    manifest {
        attributes(mapOf(
                "Specification-Title" to "EnderIO",
                "Specification-Vendor" to "SleepyTrousers",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to mod_version,
                "Implementation-Vendor" to "SleepyTrousers",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "MixinConfigs" to "mixins.enderio.json"
        ))
    }

    // Add all other source sets
    from(sourceSets.getByName("api").output)
    from(sourceSets.getByName("core").output)
    for (set in subsets) {
        from(sourceSets.getByName(set).output)
    }
}

tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")
    from(sourceSets.getByName("api").output)
}

tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.classes)
    archiveClassifier.set("sources")

    from(sourceSets.getByName("api").allJava)
    from(sourceSets.main.get().allJava)
    from(sourceSets.getByName("core").allJava)

    for (set in subsets) {
        from(sourceSets.getByName(set).allJava)
    }
}

// Add other source sets to jarJar
tasks.jarJar.configure {
    archiveClassifier.set("")

    from(sourceSets.getByName("api").output)
    from(sourceSets.getByName("core").output)
    for (set in subsets) {
        from(sourceSets.getByName(set).output)
    }
}

tasks.build {
    dependsOn(tasks.getByName("apiJar"))
    dependsOn(tasks.getByName("sourcesJar"))
    dependsOn(tasks.getByName("jarJar"))
}

if (getReleaseType() != null) {
    if (System.getenv("CHANGELOG") != null) {
        publisher {

            apiKeys {
                curseforge(System.getenv("CURSEFORGE_TOKEN"))
                modrinth(System.getenv("MODRINTH_TOKEN"))
            }

            debug.set(System.getenv("PUBLISH") != "true")

            curseID.set(curseforge_projectId)
            modrinthID.set(modrinth_projectId)
            versionType.set(getReleaseType())
            version.set(mod_version)
            displayName.set("Ender IO - $mod_version")
            changelog.set(System.getenv("CHANGELOG"))
            gameVersions.set(listOf(minecraft_version))
            loaders.set(listOf("neoforge"))
            curseEnvironment.set("both")
            artifact.set(tasks.jarJar)

            setJavaVersions("Java 17")

            curseDepends {
                optional("jei", /*"patchouli",*/ "athena", "applied-energistics-2")
            }

            modrinthDepends {
                optional(/*modrinth_dep_patchouli, */ modrinth_dep_jei, modrinth_dep_athena, modrinth_dep_ae2)
            }
        }
    } else {
        println("Release disabled, no changelog found in environment");
    }
}

publishing {
    publications {

        create<MavenPublication>("enderio") {
            groupId = "com.enderio"
            artifactId = "EnderIO"
            version = mod_version

            artifact(tasks.getByName("jar"))
            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("apiJar"))

            pom {
                name.set("Ender IO")
                description.set("Ender IO is a full-featured tech mod. It has armor, tools, weapons, machines, conduits, inventory management, mobs, etc.")
                url.set("https://github.com/SleepyTrousers/EnderIO-Rewrite")

                licenses {
                    license {
                        name.set("Unlicense")
                        url.set("https://github.com/SleepyTrousers/EnderIO-Rewrite/blob/dev/1.18.x/LICENSE.txt")
                    }
                }

                scm {
                    url.set("https://github.com/SleepyTrousers/EnderIO-Rewrite.git")
                }
            }
        }
    }

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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
    if (group != null) {
        return@withType; // neoform recompile
    }
    if (name == "compileEnsure_pluginJava") {
        //don't use the plugin to compile the plugin and open the required packages to compile it correctly, the packages are opened at compile time for other modules using EnsureSetup
        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.api=ensureplugin")
        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.code=ensureplugin")
        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.tree=ensureplugin")
        options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.util=ensureplugin")
    } else if (name != "compileJava") {
        //all modules except an unnamed one (not sure what this one is tbh)
        options.compilerArgs.add("-Xplugin:ContextEnsure")
    }
}

// ============
// Utilities
// ============

//   * enderio-1.19.1-6.0.1-alpha.jar :: release version 6.0.1-alpha for mc 1.19.1
//   * enderio-1.19.1-6.0.1.349-nightly       :: nightly build no. 349 for mc 1.19.1, based after 6.0.1.
//   * enderio-1.19.1-6.0-dev-feature-c91c8ee6e   :: dev (local) build for commit c91c8ee6e on "feature" branch for version set 6.0.
fun getVersionString(): String {
    val build_server = System.getenv("CI") != null || System.getenv("BUILD_NUMBER") != null

    if (System.getenv("BUILD_VERSION") != null) {
        var version_number = System.getenv("BUILD_VERSION")
        if (version_number.startsWith("v")) {
            version_number = version_number.substring(1)
        }

        return version_number
    }

    if (System.getenv("NIGHTLY") != null) {
        val lastVersion = getPreviousVersion()
        val lastVersionNumber = extractVersionNumber(lastVersion)

        var version_patch_lc = "0"
        if (System.getenv("BUILD_NUMBER") != null) {
            version_patch_lc = System.getenv("BUILD_NUMBER")
        }

        return "${lastVersionNumber}.${version_patch_lc}-nightly"
    }

    var version_hash = ""
    var branch_name = ""
    if (!build_server) {
        try {
            version_hash = "-" + shellRunAndRead("git rev-parse --short HEAD").trim()
        } catch (_: Exception) {
        }

        try {
            branch_name = shellRunAndRead("git rev-parse --abbrev-ref HEAD").trim()
            branch_name = "-" + branch_name.substring(branch_name.lastIndexOf("/") + 1)
        } catch (_: Exception) {
        }
    }

    return "1-dev${branch_name}${version_hash}"
}

fun getPreviousVersion(): String {
    var previous_version: String? = null
    try {
        previous_version = shellRunAndRead("git describe --abbrev=0").trim()

        if (previous_version.startsWith("v")) {
            previous_version = previous_version.substring(1)
        } else {
            previous_version = null;
        }
    } catch (_: Exception) {
    }

    if (previous_version != null) {
        return previous_version
    }

    return "1.0.0"
}

// Strips any non-numeric versioning.
fun extractVersionNumber(currentVersion: String): String {
    val getVersion = Regex("[0-9]+.[0-9]+.[0-9](?=-*)")

    val versionString = getVersion.find(currentVersion)

    if (versionString != null) {
        return versionString.value;
    }

    return "1.0.0"
}

fun shellRunAndRead(command: String): String {
    val process = ProcessBuilder()
            .command(command.split(" "))
            .directory(rootProject.projectDir)
            .start()
    return process.inputStream.bufferedReader().readText()
}

fun getReleaseType(): String? {
    // If we"re doing a proper build
    if (System.getenv("BUILD_VERSION") != null) {
        val version_string = System.getenv("BUILD_VERSION")

        if (version_string.lowercase().contains("alpha")) {
            return "alpha"
        } else if (version_string.lowercase().contains("beta")) {
            return "beta"
        }

        return "release"
    }

    return null
}

// Thanks to Mekanism for the base implementations here.

// Create and configure a new module source set.
fun setupSourceSet(name: String) {
    val sourceSet = sourceSets.create(name)

    // Add api and main modules.
    sourceSet.compileClasspath += sourceSets.getByName("api").output
    sourceSet.compileClasspath += sourceSets.main.get().output
    sourceSet.compileClasspath += sourceSets.getByName("core").output
    sourceSet.compileClasspath += sourceSets.getByName("ensure_plugin").output

    // Extend configurations
    setupExtraSourceSets(sourceSet)
}

// Thanks again to Mekanism for this stuff.
fun setupExtraSourceSets(base: SourceSet) {
    // Setup and extend configurations for alternate modules. First by making the implementation, compileOnly, runtimeOnly equivalents
    //  for those modules extend the main ones
    val baseImplementation = project.configurations.maybeCreate(base.getTaskName(null, "implementation"))
    val baseCompileOnly = project.configurations.maybeCreate(base.getTaskName(null, "compileOnly"))
    val baseRuntimeOnly = project.configurations.maybeCreate(base.getTaskName(null, "runtimeOnly"))
    baseImplementation.extendsFrom(project.configurations.getByName("implementation"))
    baseCompileOnly.extendsFrom(project.configurations.getByName("compileOnly"))
    baseRuntimeOnly.extendsFrom(project.configurations.getByName("runtimeOnly"))
}
