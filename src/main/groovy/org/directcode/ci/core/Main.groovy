package org.directcode.ci.core

import org.apache.log4j.Level as Log4jLevel
import org.apache.log4j.Logger as Log4j
import org.directcode.ci.logging.Logger
import org.directcode.ci.utils.ConsoleHandler

class Main {

    private static final logger = Logger.getLogger("Console")

    @SuppressWarnings("GroovyEmptyStatementBody")
    static void main(String[] consoleArgs) {

        /* Configure log4j to fix warnings */
        Log4j.rootLogger.level = Log4jLevel.OFF

        Thread.defaultUncaughtExceptionHandler = [
                uncaughtException: { Thread thread, Throwable e ->
                    logger.error("An unexpected error occurred in SimpleCI", e)
                }
        ] as Thread.UncaughtExceptionHandler

        def ci = CI.instance

        ci.start()

        ConsoleHandler.loop { String command, List<String> args ->
            if (command == 'build') {

                if (args.size() == 0) {
                    println "Usage: build <job>"
                    return
                }

                def jobName = args[0]

                def job = ci.jobs[jobName]

                if (job == null) {
                    println "No Such Job: ${jobName}"
                } else {
                    ci.runJob(job)
                }
            } else if (command == 'restart') {
                ci.vertxManager.stopWebServer()
                ci = null
                sleep(200)
                ci = new CI()
                ci.start()
            } else if (command == 'stop') {
                System.exit(0)
            } else if (command == 'clean') {
                if (args.size() == 0) {
                    println "Usage: clean <job>"
                    return
                }

                def jobName = args[0]

                def job = ci.jobs[jobName]

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
