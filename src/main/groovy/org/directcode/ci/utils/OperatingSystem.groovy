package org.directcode.ci.utils

import groovy.transform.CompileStatic

@CompileStatic
class OperatingSystem {
    private final String name

    OperatingSystem() {
        this(System.getProperty("os.name"))
    }

    OperatingSystem(String name) {
        this.name = name.toLowerCase()
    }

    static OperatingSystem current() {
        return new OperatingSystem()
    }

    static OperatingSystem forName(String name) {
        return new OperatingSystem(name)
    }

    boolean isWindows() {
        return name.contains("windows")
    }

    boolean isUnix() {
        return name.contains("nix") || name.contains("nux") || name.contains("aix")
    }
}
