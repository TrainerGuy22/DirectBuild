package org.directcode.ci.scm

import groovy.transform.CompileStatic

@CompileStatic
class Changelog {
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
