package org.directcode.ci.jobs

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.directcode.ci.core.CI

@CompileStatic
class JobHistory {
    private final List<Entry> entries = []
    private final Job job

    JobHistory(Job job) {
        this.job = job
    }

    void load() {
        def history = ((List<Map<String, Object>>) CI.instance.storage.get("job_history").get(job.name, []))
        for (result in history) {
            def entry = new Entry()
            entries.add(entry)
            entry.number = result.number as int
            entry.log = result.log as String
            entry.when = result.timeStamp as String
            entry.status = result.status as int
        }
    }

    def getEntries() {
        return entries
    }

    def getLatestBuild() {
        entries.empty ? null : entries.last()
    }

    @Override
    String toString() {
        entries.join("\n")
    }

    @ToString
    static class Entry {
        int id, status, number
        String log
        String when
    }
}
