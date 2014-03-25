package org.directcode.ci.scm

import org.directcode.ci.api.SCM
import org.directcode.ci.exception.ToolMissingException
import org.directcode.ci.utils.Utils

class GitSCM extends SCM {

    void gitClone() {
        def cmd = [findGit().absolutePath, "clone", "--recursive", job.SCM.url as String, job.buildDir.absolutePath]

        run(cmd)

        updateSubmodules()
    }

    void update() {
        def cmd = [findGit().absolutePath, "pull", "--all"]

        updateSubmodules()

        run(cmd)
    }

    boolean exists() {
        def gitDir = new File(job.buildDir, ".git")

        return gitDir.exists()
    }

    @Override
    void execute() {
        if (exists()) {
            update()
        } else {
            gitClone()
        }
    }

    @Override
    Changelog changelog(int count = 4) {
        def changelog = new Changelog()

        if (!exists()) {
            gitClone()
        }

        def proc = execute([findGit().absolutePath, "log", "-${count}".toString(), "--pretty=%H%n%an%n%s"])

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

    void updateSubmodules() {
        run([findGit().absolutePath, "submodule", "update", "--init", "--recursive"])
    }

    Process execute(List<String> command) {
        def builder = new ProcessBuilder(command)
        builder.directory(job.buildDir)
        builder.redirectErrorStream(true)
        return builder.start()
    }
}
