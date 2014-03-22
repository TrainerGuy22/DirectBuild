package com.directmyfile.ci.utils

class FileSpec {
    private final FileMatcher matcher

    List<File> files = []

    FileSpec(File parent) {
        this.matcher = FileMatcher.create(parent)
    }

    void include(String location) {
        include new File(matcher.parent, location)
    }

    void include(File file) {
        files << file
    }
}
