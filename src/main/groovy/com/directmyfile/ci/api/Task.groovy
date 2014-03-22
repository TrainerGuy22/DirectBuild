package com.directmyfile.ci.api

import com.directmyfile.ci.core.CI
import com.directmyfile.ci.jobs.Job
import com.directmyfile.ci.jobs.JobLog

/**
 * A CI build Task
 */
abstract class Task {
    CI ci
    Job job
    JobLog log

    /**
     * Executes this Task
     * @param params The JSON object of this task - Includes two more types: job, and ci
     * @return
     */
    abstract void execute();

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
