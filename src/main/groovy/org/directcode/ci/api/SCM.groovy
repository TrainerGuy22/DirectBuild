package org.directcode.ci.api

import org.directcode.ci.core.CI
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog
import org.directcode.ci.scm.Changelog

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
     * @return SCM Changelog
     */
    abstract Changelog changelog();

    int run(List<String> command, File directory = job.buildDir, Map<String, String> env = [:]) {
        CI.logger.debug("Executing: '${command.join(" ")}'")
        log.write("Executing Command: '${command.join(' ')}'")
        def builder = new ProcessBuilder().command(command)
        builder.directory(directory)
        builder.environment().putAll(env)
        builder.redirectErrorStream(true)
        def proc = builder.start()
        proc.inputStream.eachLine { line ->
            CI.logger.debug(line)
            log.write(line)
        }
        def exitCode = proc.waitFor()
        log.write("Process Exited with Code: ${exitCode}")
        return exitCode
    }
}
