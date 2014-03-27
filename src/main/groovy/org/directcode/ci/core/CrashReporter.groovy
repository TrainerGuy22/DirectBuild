package org.directcode.ci.core

import jpower.core.Task
import jpower.core.Worker

/**
 * Reports Exceptions from SimpleCI to the crash reporter
 */
class CrashReporter {
    static
    final String reporter = "https://script.google.com/macros/s/AKfycby4kKJjBVLyrfS83qec8_nJBzSWN2LKqfNMDzBsph_R20tfOhc/exec"

    private static Worker worker = new Worker()

    static void queue(String log, Closure handler) {
        if (!worker.running) {
            worker.start()
        }
        worker.addTask(new Task() {
            @Override
            void execute() {
                def url = reporter.toURL()
                def connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.connect()
                connection.outputStream.withWriter { it ->
                    it.write("log=" + log)
                }
                def id = connection.inputStream.text
                connection.disconnect()
                handler(id)
                System.exit(1)
            }
        })
    }

    static void report(Throwable exception, Closure handler) {
        def writer = new StringWriter()
        def out = new PrintWriter(writer)
        exception.printStackTrace(out)
        def content = writer.toString()
        queue(content, handler)
    }
}