package org.directcode.ci.api

import org.directcode.ci.core.CI
import org.directcode.ci.exception.TaskFailedException
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog
import org.directcode.ci.scm.Changelog
import org.directcode.ci.utils.Utils

/**
 * A Source Code Manager
 */
abstract class SCM {

    CI ci
    Job job
    JobLog log

    abstract void execute();

    /**
     * Makes Changelog from SCM
     * @param count Changelog Entry Count
     * @return SCM Changelog
     */
    abstract Changelog changelog(int count);

    int run(List<String> command, File workingDir = job.buildDir, Map<String, String> env = [:], boolean handleExitCode = true) {
        CI.logger.debug("Executing: '${command.join(" ")}'")
        log.write("\$ '${command.join(' ')}'")

        def result = Utils.execute { ->
            executable(command[0])
            arguments(command.drop(1))
            directory(workingDir)
            environment(env)
            streamOutput { line ->
                ci.logger.debug("${line}")
                log.write("${line}")
            }
        }

        log.write(">> Command Complete { code: ${result.code} }")

        if (handleExitCode && result.code != 0) {
            throw new TaskFailedException("Command exited with non-zero status!")
        }
        return result.code
    }
}
