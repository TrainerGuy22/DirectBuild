package org.directcode.ci.api

import org.directcode.ci.core.CI
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog

/**
 * A CI build Task
 */
abstract class Task {
    CI ci
    Job job
    JobLog log

    /**
     * Executes this Task
     */
    abstract void execute();

    /**
     * Configures this Task
     * @param closure closure that will configure the task
     */
    abstract void configure(Closure closure);

    static File file(File parent = new File("."), String name) {
        return new File(parent, name)
    }

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
