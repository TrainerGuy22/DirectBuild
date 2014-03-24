package org.directcode.ci.tasks

import org.directcode.ci.api.Task
import org.directcode.ci.exception.TaskFailedException

class MavenTask extends Task {
    List<String> tasks = []
    List<String> opts = []
    String mavenCommand = "mvn"

    @Override
    void execute() {
        def cmd = [ mavenCommand ]

        cmd.addAll(opts)

        cmd.addAll(tasks)

        def exitCode = run(cmd)

        if (exitCode != 0) {
            throw new TaskFailedException("Maven exited with a non-zero status!")
        }
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}