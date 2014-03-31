package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.apache.log4j.Level as Log4jLevel
import org.apache.log4j.Logger as Log4j
import org.directcode.ci.jobs.Job
import org.directcode.ci.logging.LogLevel
import org.directcode.ci.logging.Logger
import org.directcode.ci.utils.ConsoleHandler
import org.directcode.ci.utils.OperatingSystem
import org.directcode.ci.utils.Utils
import org.jetbrains.annotations.NotNull

@CompileStatic
class Main {

    static final Logger logger = Logger.getLogger("Console")

    @SuppressWarnings("GroovyEmptyStatementBody")
    static void main(@NotNull String[] consoleArgs) {

        if (OperatingSystem.current().unsupported) {
            logger.warning("SimpleCI does not officially support your platform.")
        }

        /* Configure log4j to fix warnings */
        Log4j.rootLogger.level = Log4jLevel.OFF

        Thread.defaultUncaughtExceptionHandler = [
                uncaughtException: { Thread thread, Throwable e ->
                    if (logger.canLog(LogLevel.DEBUG)) {
                        e.printStackTrace()
                        System.exit(1)
                        return
                    }
                    def output = new File("ci.log").toPath()
                    output.append("${Utils.exceptionToString(e)}")
                    CrashReporter.report(output)
                }
        ] as Thread.UncaughtExceptionHandler

        CI ci = CI.instance

        ci.start()

        ConsoleHandler.loop { String command, List<String> args ->
            if (command == 'build') {

                if (args.size() == 0) {
                    println "Usage: build <job>"
                    return
                }

                def jobName = args[0]

                def job = ci.jobs[jobName] as Job

                if (job == null) {
                    println "No Such Job: ${jobName}"
                } else {
                    ci.runJob(job)
                }
            } else if (command == 'stop') {
                System.exit(0)
            } else if (command == 'clean') {
                if (args.size() == 0) {
                    println "Usage: clean <job>"
                    return
                }

                def jobName = args[0]

                Job job = ci.jobs[jobName]

                if (job == null) {
                    println "No Such Job: ${jobName}"
                } else {
                    ci.logger.info "Cleaning Workspace for Job '${jobName}'"
                    job.buildDir.deleteDir()
                }
            }
        }
    }
}
