package com.directmyfile.ci

import org.apache.log4j.Level
import org.apache.log4j.Logger

class Main {
    static void main(String[] args) {
        try {
            Logger.getRootLogger().setLevel(Level.INFO)

            def ci = new CI()
            ci.start()
            def reader = System.in.newReader()

            reader.eachLine {
                def split = it.tokenize(' ')

                if (split[0] == 'run') {
                    def jobName = split[1]

                    def job = ci.jobs.get(jobName)

                    if (job == null) {
                        println "No Such Job: ${jobName}"
                    } else {
                        ci.runJob(job)
                    }
                } else if (split[0] == 'restart') {
                    ci.vertxManager.stopWebServer()
                    ci = null
                    System.gc()
                    ci = new CI()
                    ci.start()
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("SimpleCI has encountered an error!", e)
        }
    }
}
