package org.directcode.ci.source

import groovy.transform.CompileStatic

/**
 * A Changelog for VCS Sources
 */
@CompileStatic
class VCSChangelog {
    List<Entry> entries = []

    Entry newEntry() {
        def entry = new Entry()
        entries.add(entry)
        return entry
    }

    static class Entry {
        String revision
        String message
        String author
    }
}
