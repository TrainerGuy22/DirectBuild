package org.directcode.ci.scm

import org.directcode.ci.api.SCM
import org.directcode.ci.exception.ToolMissingException
import org.directcode.ci.jobs.Job
import org.directcode.ci.utils.Utils

class GitSCM extends SCM {

    void clone(Job job) {
        def cmd = [findGit().absolutePath, "clone", "--recursive", job.SCM.url as String, job.buildDir.absolutePath]

        def proc = execute(cmd)
        job.logFile.parentFile.mkdirs()
        def log = job.logFile.newPrintWriter()
        proc.inputStream.eachLine {
            log.println(it)
            log.flush()
        }
        log.println()
        log.flush()
        log.close()
        def exitCode = proc.waitFor()
        if (exitCode != 0) {
            throw new ToolMissingException("Git failed to clone repository!")
        }
        updateSubmodules()
    }

    void update() {
        def cmd = [findGit().absolutePath, "pull", "--all"]

        updateSubmodules()

        def proc = execute(cmd)

        def log = job.logFile.newPrintWriter()
        proc.inputStream.eachLine {
            log.println(it)
            log.flush()
        }
        def exitCode = proc.waitFor()
        log.println()
        log.flush()
        log.close()
        if (exitCode != 0) {
            throw new ToolMissingException("Git failed to pull changes!")
        }
    }

    boolean exists() {
        def gitDir = new File(job.buildDir, ".git")

        return gitDir.exists()
    }

    void execute() {
        if (exists()) {
            update()
        } else {
            clone()
        }
    }

    @Override
    Changelog changelog() {
        def changelog = new Changelog()

        if (!exists()) {
            clone(job)
        }

        def proc = execute([findGit().absolutePath, "log", "-4", "--pretty=%H%n%an%n%s"])

        proc.waitFor()

        def log = proc.text.readLines()

        def current = changelog.newEntry()
        def type = 1
        for (entry in log) {
            //noinspection GroovySwitchStatementWithNoDefault
            switch (type) {
                case 1:
                    type++
                    current.revision = entry
                    break
                case 2:
                    type++
                    current.author = entry
                    break
                case 3:
                    type = 1
                    current.message = entry
                    current = changelog.newEntry()
                    break
            }
        }

        changelog.entries.removeAll { entry ->
            !entry.message || !entry.revision || !entry.author
        }

        return changelog
    }

    static File findGit() {
        def gitCommand = Utils.findCommandOnPath("git")
        if (gitCommand == null) {
            throw new ToolMissingException("Could not find Git on System!")
        }
        return gitCommand
    }

    boolean updateSubmodules() {
        return execute([findGit().absolutePath, "submodule", "update", "--init", "--recursive"]).waitFor() == 0
    }

    Process execute(List<String> command) {
        def builder = new ProcessBuilder(command)
        builder.directory(job.buildDir)
        builder.redirectErrorStream(true)
        return builder.start()
    }
}
