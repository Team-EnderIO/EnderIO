package com.enderio

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.tasks.util.PatternFilterable

// Credit: Mekanism for the base concept.

class CombineJars {
    static List<String> getPathsToExclude(Project project, boolean mainProject) {
        List<String> toExclude = new ArrayList<>()
        toExclude.add('META-INF/mods.toml')
        toExclude.add('META-INF/accesstransformer.cfg')
        toExclude.add('.cache/cache')

        // Exclude duplicated data
        def resourcesDir = project.rootProject.sourceSets.main.output.resourcesDir
        int baseLength = "$resourcesDir".length()
        project.fileTree(dir: "$resourcesDir/data/").each {
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
    static void mergeResources(Project rootProject, List<Project> toMerge) {
        // Create resource directories
        def resourcesDir = rootProject.sourceSets.main.output.resourcesDir
        rootProject.mkdir("$resourcesDir/META-INF")

        // Delete data resources to prevent pollution
        rootProject.file("$resourcesDir/data").deleteDir()
        rootProject.mkdir("$resourcesDir/data")

        new File(resourcesDir, "META-INF/mods.toml") << mergeManifests(toMerge)
        new File(resourcesDir, "META-INF/accesstransformer.cfg") << mergeATs(toMerge)

        mergeTags(rootProject, toMerge)
    }

    private static String mergeManifests(List<Project> toMerge) {
        def modsToml = ""
        toMerge.each { subproject ->
            def moduleToml = new File(subproject.sourceSets.main.output.resourcesDir, 'META-INF/mods.toml').text
            if (modsToml.isEmpty()) {
                modsToml += moduleToml
            } else {
                def splitLines = moduleToml.split('\n')

                for (def i = 4; i < splitLines.length; i++) {
                    modsToml += '\n' + splitLines[i]
                }
            }
        }
        return modsToml
    }

    private static String mergeATs(List<Project> toMerge) {
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
    private static void mergeTags(Project rootProject, List<Project> toMerge) {
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
    }

    private static void mergeTag(Project rootProject, String tag, List<String> tagPaths) {
        println(tag + " appeared " + tagPaths.size() + " times")
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
            File outputFile = new File("$rootProject.sourceSets.main.output.resourcesDir" + tag)
            //Make all parent directories needed
            outputFile.getParentFile().mkdirs()
            outputFile.text = JsonOutput.toJson(outputTagAsJson)
        }
    }
}
