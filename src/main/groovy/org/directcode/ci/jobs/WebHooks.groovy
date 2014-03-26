package org.directcode.ci.jobs

import org.directcode.ci.utils.Utils

class WebHooks {
    private Job job
    private List<WebHook> jobDone = []
    private List<WebHook> jobStarted = []

    protected WebHooks(Job job) {
        this.job = job

        job.ci.eventBus.on("ci.job.done") { event ->
            if (event.jobName == job.name) {
                for (hook in jobDone) {
                    hook(event)
                }
            }
        }

        job.ci.eventBus.on("ci.job.running") { event ->
            if (event.jobName == job.name) {
                for (hook in jobStarted) {
                    hook(event)
                }
            }
        }
    }

    void done(String url) {
        jobDone.add(new WebHook(url))
    }

    void start(String url) {
        jobStarted.add(new WebHook(url))
    }

    class WebHook {
        String url

        WebHook(String url) {
            this.url = url
        }

        void call(Object data) {
            def connection = url.toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connect()
            connection.outputStream.withPrintWriter { w ->
                w.write(Utils.encodeJSON(data))
            }
            connection.disconnect()
        }

        void url(String url) {
            this.url = url
        }

        void call(@DelegatesTo(WebHook) Closure closure) {
            closure.delegate = this
            closure()
        }
    }
}
