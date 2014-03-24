package org.directcode.ci.utils

class OperatingSystem {
    private String name = System.getProperty("os.name").toLowerCase()

    static OperatingSystem current() {
        return new OperatingSystem()
    }

    boolean isWindows() {
        return name.contains("windows")
    }

    boolean isUnix() {
        return name.contains("nix") || name.contains("nux") || name.contains("aix")
    }
}
