package com.directmyfile.ci.tasks

import com.directmyfile.ci.api.Task

class MakeTask extends Task {

    List<String> targets = []

    @Override
    void execute() {
        def command = [ "make" ]
        command.addAll(targets)
        run(command)
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
