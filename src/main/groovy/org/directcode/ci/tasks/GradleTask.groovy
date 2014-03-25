package org.directcode.ci.tasks

import org.directcode.ci.api.Task
import org.directcode.ci.exception.ToolMissingException
import org.directcode.ci.utils.OperatingSystem
import org.directcode.ci.utils.Utils

class GradleTask extends Task {
    boolean wrapper = false
    List<String> opts = []
    List<String> tasks = []

    @Override
    void execute() {
        def command = []

        if (wrapper) {
            if (!file(job.buildDir, "gradlew").exists()) {
                throw new ToolMissingException("Gradle Wrapper not found in Job: ${job.name}")
            }

            if (OperatingSystem.current().unix) {
                command.add("sh")
            }

            command.add("gradlew")
        } else {
            def c = Utils.findCommandOnPath("gradle")
            if (c == null) {
                throw new ToolMissingException("Gradle not found on this system.")
            }
            command.add(c.absolutePath)
        }

        command.addAll(opts)
        command.addAll(tasks)

        run(command, job.buildDir, [TERM: "dumb"])
    }

    @Override
    void configure(@DelegatesTo(GradleTask) Closure closure) {
        with(closure)
    }
}
