package org.directcode.ci.tasks

import org.directcode.ci.api.Task
import org.directcode.ci.exception.TaskFailedException

/**
 * Executes a Command
 */
class CommandTask extends Task {

    String command

    @Override
    void execute() {
        def exitCode = run(command.tokenize())
        if (exitCode != 0) {
            throw new TaskFailedException("Process Exited with Code: ${exitCode}")
        }
    }

    @Override
    void configure(Closure closure) {
        closure.delegate = this
        closure()
    }
}
