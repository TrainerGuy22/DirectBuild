package org.directcode.ci.jobs

import groovy.transform.CompileStatic
import org.directcode.ci.utils.HTTP
import org.directcode.ci.utils.Utils

@CompileStatic
class WebHooks {
    private Job job
    private List<WebHook> jobDone = []
    private List<WebHook> jobStarted = []

    protected WebHooks(Job job) {
        this.job = job

        job.ci.eventBus.on("ci.job.done") { Map<String, ? extends Object> event ->
            if (event["jobName"] == job.name) {
                for (hook in jobDone) {
                    hook(event)
                }
            }
        }

        job.ci.eventBus.on("ci.job.running") { Map<String, ? extends Object> event ->
            if (event["jobName"] == job.name) {
                for (hook in jobStarted) {
                    hook(event)
                }
            }
        }
    }

    void done(String url, String type = "POST") {
        jobDone.add(new WebHook(url, Type.parse(type)))
    }

    void start(String url, String type = "POST") {
        jobStarted.add(new WebHook(url, Type.parse(type)))
    }

    class WebHook {
        String url
        Type type = Type.GET

        WebHook(String url, Type type = Type.GET) {
            this.url = url
            this.type = type
        }

        void call(Object data) {
            if (type == Type.GET) {
                HTTP.get(url: url.replace('{JSON}', Utils.urlEncode(Utils.encodeJSON(data)) as String))
            } else {
                HTTP.post(url: url, data: [data: Utils.encodeJSON(data)])
            }
        }

        void url(String url) {
            this.url = url
        }

        void type(String name) {
            this.type = Type.parse(name)
        }

        void call(@DelegatesTo(WebHook) Closure closure) {
            closure.delegate = this
            closure()
        }
    }

    enum Type {
        GET, POST

        static Type parse(String name) {
            return values().find { it ->
                it.name().toLowerCase() == name.toLowerCase()
            } ?: GET
        }
    }
}
