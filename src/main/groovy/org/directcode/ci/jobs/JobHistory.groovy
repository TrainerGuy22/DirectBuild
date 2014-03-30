package org.directcode.ci.jobs

import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.directcode.ci.core.CI
import org.directcode.ci.utils.FileMatcher

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
            def artifactDir = new File(job.ci.artifactDir, "${job.name}/${entry.number}")
            if (!artifactDir.exists()) {
                artifactDir.mkdirs()
            }
            def files = FileMatcher.create(artifactDir).recursive(FileType.FILES)
            files.each { file ->
                def a = new Artifact()
                a.name = file.absolutePath.replace(artifactDir.absolutePath + "/", "")
                entry.artifacts.add(a)
            }
        }
    }

    List<Entry> getEntries() {
        return entries
    }

    Entry getLatestBuild() {
        entries.empty ? null : entries.last()
    }

    @Override
    String toString() {
        entries.join("\n")
    }

    @ToString
    @CompileStatic
    static class Entry {
        int id, status, number
        String log
        String when
        List<Artifact> artifacts = []
    }

    static class Artifact {
        String name
    }
}
