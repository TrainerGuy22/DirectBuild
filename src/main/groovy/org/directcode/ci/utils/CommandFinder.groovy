package org.directcode.ci.utils

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI

@CompileStatic
class CommandFinder {
    static File find(String command) {
        def path = getPath(command)
        if (path == null) {
            path = findOnPath(command)
        }
        return path
    }

    static File getPath(String entryName) {
        def paths = CI.instance.config.pathsSection()
        if (paths.containsKey(entryName)) {
            return new File(paths[entryName]).absoluteFile
        } else {
            return null
        }
    }

    static File findOnPath(String command) {
        command = actualCommand(command)
        def systemPath = System.getenv("PATH")
        def pathDirs = systemPath.split(File.pathSeparator)

        File executable = null
        for (pathDir in pathDirs) {
            def file = new File(pathDir, command)
            if (file.file && file.canExecute()) {
                executable = file
                break
            }
        }
        return executable?.absoluteFile
    }

    static String actualCommand(String command) {
        if (OperatingSystem.current().windows) {
            return "${command}.exe"
        } else {
            return command
        }
    }

    static File forScript(File base, String windows, String unix) {
        if (OperatingSystem.current().windows) {
            return new File(base, windows).absoluteFile
        } else {
            return new File(base, unix).absoluteFile
        }
    }

    static File shell() {
        if (OperatingSystem.current().windows) {
            return find("cmd")
        } else {
            return find("shell") ?: find("bash") ?: find("sh")
        }
    }
}
