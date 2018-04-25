#!/usr/bin/groovy
package org.centos.pipeline

def keysToList(keyValue) {
    // Convert map keys to list
    keys = []
    keyValue.each { key, value ->
        keys.add(key)
    }
    return keys
}

def matchPath(path, paths){
    for (int i = 0; i < paths.size(); i++) {
        if (path ==~ ~/${paths[i]}/) {
            return true
        }
    }
    return false
}

def getTargetsToTest(targetsMap) {
    def targets = [:]
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            def files = new ArrayList(entry.affectedFiles)
            for (int k = 0; k <files.size(); k++) {
                def this_match = false
                def file = files[k]
                for (e in targetsMap) {
                    if (matchPath(file.path, e.value)) {
                        this_match = true
                        targets[e.key] = 1
                        break
                    }
                }
                if (!this_match) {
                    // If we get here then we have a non-target specific change
                    // and all targets should be tested.
                    println "Non-target file matched, will test all targets"
                    return keysToList(targetsMap)
                }
            }
        }
    }
    return keysToList(targets)
}
