package org.directcode.ci.tasks

import groovy.transform.CompileStatic
import org.directcode.ci.api.Task

@CompileStatic
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