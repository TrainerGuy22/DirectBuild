package org.directcode.ci.tasks

import org.directcode.ci.api.Task

/**
 * Executes a Command
 */
class CommandTask extends Task {

    String command

    @Override
    void execute() {
        run(command.tokenize())
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
