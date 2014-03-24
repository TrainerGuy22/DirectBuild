package org.directcode.ci.tasks

import org.directcode.ci.api.Task
import org.directcode.ci.exception.TaskFailedException

class GitTask extends Task {
    List<String> args = []

    @Override
    void execute() {
        def cmd = ["git"]
        cmd.addAll(args)
        if (run(cmd) != 0) {
            throw new TaskFailedException("Git exited with a non-zero status!")
        }
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
