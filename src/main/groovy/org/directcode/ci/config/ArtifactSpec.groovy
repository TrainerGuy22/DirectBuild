package org.directcode.ci.config

class ArtifactSpec {
    final List<String> files = []
    final List<String> directories = []

    void file(String name) {
        files.add(name)
    }

    void directory(String name) {
        directories.add(name)
    }
}
