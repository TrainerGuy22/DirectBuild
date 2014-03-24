package org.directcode.ci.tasks

import org.directcode.ci.api.Task
import org.directcode.ci.exception.TaskFailedException

class AntTask extends Task {
    List<String> tasks = []
    List<String> opts = []
    String antCommand = "ant"

    @Override
    void execute() {
        def cmd = [ antCommand ]

        cmd.addAll(opts)

        cmd.addAll(tasks)

        def exitCode = run(cmd)

        if (exitCode != 0) {
            throw new TaskFailedException("Ant exited with a non-zero status!")
        }
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}