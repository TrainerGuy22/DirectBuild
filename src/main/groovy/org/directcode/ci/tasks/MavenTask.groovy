package org.directcode.ci.tasks

import org.directcode.ci.api.Task

class MavenTask extends Task {
    List<String> tasks = []
    List<String> opts = []
    String mavenCommand = "mvn"

    @Override
    void execute() {
        def cmd = [mavenCommand]

        cmd.addAll(opts)

        cmd.addAll(tasks)

        run(cmd)
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}