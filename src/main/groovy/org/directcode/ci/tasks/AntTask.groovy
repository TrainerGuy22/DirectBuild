package org.directcode.ci.tasks

import groovy.transform.CompileStatic
import org.directcode.ci.api.Task

@CompileStatic
class AntTask extends Task {
    List<String> tasks = []
    List<String> opts = []
    String antCommand = "ant"

    @Override
    void execute() {
        def cmd = [antCommand]

        cmd.addAll(opts)

        cmd.addAll(tasks)

        run(cmd)
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}