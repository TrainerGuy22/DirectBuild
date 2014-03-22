package com.directmyfile.ci.tasks

import com.directmyfile.ci.api.Task
import com.directmyfile.ci.exception.JobConfigurationException
import com.directmyfile.ci.exception.TaskFailedException
import com.directmyfile.ci.exception.ToolException
import com.directmyfile.ci.utils.Utils

class GradleTask extends Task {
    boolean wrapper = false
    List<String> opts = []
    List<String> tasks = []

    @Override
    void execute() {
        def command = []

        if (wrapper) {
            if (!file(job.buildDir, "gradlew").exists()) {
                throw new JobConfigurationException("Gradle Wrapper not found in Job: ${job.name}")
            }

            command.addAll("sh", "gradlew")
        } else {
            def c = Utils.findCommandOnPath("gradle")
            if (c == null) {
                throw new ToolException("Gradle not found on this system.")
            }
            command.add(c.absolutePath)
        }

        command.addAll(opts)
        command.addAll(tasks)

        def exitCode = run(command, job.buildDir, [ TERM: "dumb" ])

        if (exitCode != 0) {
            throw new TaskFailedException("Gradle exited with a non-zero status!")
        }
    }

    @Override
    void configure(@DelegatesTo(GradleTask) Closure closure) {
        with(closure)
    }
}
