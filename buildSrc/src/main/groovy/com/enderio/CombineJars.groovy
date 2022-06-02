package com.enderio

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.util.PatternFilterable

// Credit: Mekanism for the base concept.

class CombineJars {
    static List<String> getPathsToExclude(Project project, boolean mainProject) {
        List<String> toExclude = new ArrayList<>()
        toExclude.add('META-INF/mods.toml')
        toExclude.add('META-INF/accesstransformer.cfg')
        toExclude.add('.cache/cache')

        // Exclude duplicated data
        int baseLength = "$project.rootProject.buildDir/generated/".length()
        project.fileTree(dir: "$project.rootProject.buildDir/generated/data/").each {
            File file -> toExclude.add(file.getPath().substring(baseLength))
        }

        if (!mainProject) {
            toExclude.add('logo.png')
            toExclude.add('pack.mcmeta')
        }

        return toExclude
    }

    /**
     * Merge mod resources together.
     */
    static void mergeResources(Project rootProject, Set<Project> toMerge) {
        println("[Mod Merger] Merging mod resources.")

        // Create resource directories
        def generatedDir = "$rootProject.buildDir/generated"
        rootProject.mkdir("$generatedDir/META-INF")

        // Delete data resources to prevent pollution
        rootProject.file("$generatedDir/data").deleteDir()
        rootProject.mkdir("$generatedDir/data")

        new File("$generatedDir/META-INF/mods.toml") << mergeManifests(toMerge)
        new File("$generatedDir/META-INF/accesstransformer.cfg") << mergeATs(toMerge)

        mergeTags(rootProject, toMerge)

        println("[Mod Merger] Merged mod resources.")
    }

    static Closure getGeneratedClosure() {
        return { CopySpec c ->
            c.include('META-INF/mods.toml', 'META-INF/accesstransformer.cfg', 'data/**')
        }
    }

    private static String mergeManifests(Set<Project> toMerge) {
        println("[Mod Merger - TOMLs] Merged mods.toml's.")
        def modsToml = ""

        for (Project subproject : toMerge) {
            subproject.sourceSets.main.resources.matching { PatternFilterable pf ->
                pf.include('META-INF/mods.toml')
            }.each { file ->
                if (modsToml.isEmpty()) {
                    modsToml += file.getText()
                } else {
                    def splitLines = file.getText().split('\n')

                    for (def i = 4; i < splitLines.length; i++) {
                        modsToml += '\n' + splitLines[i]
                    }
                }
            }
        }

        return modsToml
    }

    private static String mergeATs(Set<Project> toMerge) {
        println("[Mod Merger - ATs] Merging access transformers.")
        def ats = ""
        toMerge.each { subproject ->
            def moduleATs = new File(subproject.sourceSets.main.output.resourcesDir, 'META-INF/accesstransformer.cfg')
            if (moduleATs.exists()) {
                ats += moduleATs.text
            }
        }
        return ats
    }

    // More from Mekanism, thank you!
    private static void mergeTags(Project rootProject, Set<Project> toMerge) {
        println("[Mod Merger - Tags] Merging tags")
        Closure tagFilter = { PatternFilterable pf -> pf.include('**/data/*/tags/**/*.json') }
        Map<String, List<String>> reverseTags = new HashMap<>()
        for (Project subProject : toMerge) {
            subProject.sourceSets.main.resources.srcDirs.each { srcDir ->
                int srcDirPathLength = srcDir.getPath().length()
                rootProject.fileTree(srcDir).matching(tagFilter).each { file ->
                    //Add the sourceSet to the reverse lookup
                    String path = file.getPath()
                    String tag = path.substring(srcDirPathLength)
                    if (!reverseTags.containsKey(tag)) {
                        reverseTags.put(tag, new ArrayList<>())
                    }
                    reverseTags.get(tag).add(path)
                }
            }
        }
        //Go through the reverse tag index and if there are multiple sourceSets that contain the same tag
        // properly merge that tag
        reverseTags.each { tag, tagPaths ->
            if (tagPaths.size() > 1) {
                mergeTag(rootProject, tag, tagPaths)
            }
        }
        println("[Mod Merger - Tags] Merged tags.")
    }

    private static void mergeTag(Project rootProject, String tag, List<String> tagPaths) {
        println("[Mod Merger - Tags] " + tag + " appeared " + tagPaths.size() + " times")
        Object outputTagAsJson = null
        tagPaths.each { path ->
            Object tagAsJson = new JsonSlurper().parse(rootProject.file(path))
            if (outputTagAsJson == null) {
                outputTagAsJson = tagAsJson
            } else {
                outputTagAsJson.values += tagAsJson.values
            }
        }
        if (outputTagAsJson != null) {
            File outputFile = new File("$rootProject.buildDir/generated" + tag)
            //Make all parent directories needed
            outputFile.getParentFile().mkdirs()
            outputFile.text = JsonOutput.toJson(outputTagAsJson)
        }
    }
}
