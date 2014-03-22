package com.directmyfile.ci.tasks

import com.directmyfile.ci.api.Task
import com.directmyfile.ci.exception.TaskFailedException

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
