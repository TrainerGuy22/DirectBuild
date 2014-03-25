package org.directcode.ci.tasks

import org.directcode.ci.api.Task

class GitTask extends Task {
    List<String> args = []

    @Override
    void execute() {
        def cmd = ["git"]
        cmd.addAll(args)
        run(cmd)
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
