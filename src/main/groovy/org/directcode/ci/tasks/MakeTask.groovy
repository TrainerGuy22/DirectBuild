package org.directcode.ci.tasks

import groovy.transform.CompileStatic
import org.directcode.ci.api.Task

@CompileStatic
class MakeTask extends Task {

    List<String> targets = []

    @Override
    void execute() {
        def command = ["make"]
        command.addAll(targets)
        run(command)
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
