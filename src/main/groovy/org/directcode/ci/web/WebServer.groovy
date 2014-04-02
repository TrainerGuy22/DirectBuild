package org.directcode.ci.web

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import org.directcode.ci.core.CI
import org.directcode.ci.utils.Utils
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher

@CompileStatic
class WebServer {
    final HttpServer server
    final Vertx vertx

    WebServer() {
        vertx = Vertx.newVertx()
        server = vertx.createHttpServer()
    }

    void start(int port, String ip) {
        def matcher = new RouteMatcher()
        configure(matcher)
        server.requestHandler(matcher.asClosure())
        server.listen(port, ip)
    }

    private void configure(RouteMatcher matcher) {
        def ci = CI.get()

        matcher.get('/') { HttpServerRequest r ->
            writeResource(r, "index.html")
        }

        matcher.get('/css/:file') { HttpServerRequest r ->
            writeResource(r, "css/${r.params['file']}")
        }

        matcher.get('/js/:file') { HttpServerRequest r ->
            writeResource(r, "js/${r.params['file']}")
        }

        matcher.get('/img/:file') { HttpServerRequest r ->
            writeResource(r, "img/${r.params['file']}")
        }

        matcher.get('/fonts/:file') { HttpServerRequest r ->
            writeResource(r, "fonts/${r.params['file']}")
        }

        matcher.get('/job/:name') { HttpServerRequest r ->
            writeResource(r, "job.html")
        }

        matcher.get('/api/log/:job') { HttpServerRequest request ->
            def jobName = request.params['job'] as String

            if (!ci.jobs.containsKey(jobName)) {
                writeResource(request, "404.html"); return
            }

            def job = ci.jobs[jobName]

            if (!job.logFile.exists()) {
                writeResource(request, "404.html")
            } else {
                request.response.sendFile(job.logFile.absolutePath)
            }
        }

        matcher.get('/hook/:name') { HttpServerRequest it ->
            def jobName = it.params['name'] as String
            it.response.end('')

            if (!ci.jobs.containsKey(jobName)) {
                it.response.end(new JsonBuilder([
                        error: "Job does not exist!"
                ]).toPrettyString())
            }

            it.response.end(new JsonBuilder([
                    error: null,
            ]).toPrettyString())

            def job = ci.jobs[jobName]

            ci.logger.info "Job Hook executing job ${jobName}"

            ci.runJob(job)
        }

        matcher.get('/artifact/:job/:id/:name') { HttpServerRequest request ->
            def jobName = request.params['job'] as String
            def artifact = request.params['name'] as String
            def id = request.params['id'] as String
            if (!ci.jobs.containsKey(jobName)) {
                writeResource(request, "404.html"); return
            }

            def artifactFile = new File(ci.artifactDir, "${jobName}/${id}/${artifact}")

            if (!artifactFile.exists()) {
                writeResource(request, "404.html"); return
            }

            request.response.sendFile(artifactFile.absolutePath)
        }

        matcher.get('/jobs') { HttpServerRequest r ->
            writeResource(r, "jobs.html")
        }

        matcher.get('/api/jobs') { HttpServerRequest r ->
            def jobInfo = []

            ci.jobs.values().each { job ->
                jobInfo += [
                        name  : job.name,
                        status: job.status.ordinal()
                ]
            }

            r.response.end(Utils.encodeJSON(jobInfo) as String)
        }

        matcher.post('/github/:name') { HttpServerRequest it ->
            def jobName = it.params['name'] as String
            it.response.end('')

            if (!ci.jobs.containsKey(jobName)) return

            def job = ci.jobs[jobName]

            ci.logger.info "GitHub Hook executing job ${jobName}"

            ci.runJob(job)
        }

        matcher.get('/api/history/:name') { HttpServerRequest r ->
            def jobName = r.params['name'] as String

            if (!ci.jobs.containsKey(jobName)) {
                r.response.end(Utils.encodeJSON([
                        code : 404,
                        error: [
                                message: "Job Not Found"
                        ]
                ]) as String)
                return
            }

            def job = ci.jobs[jobName]

            r.response.end(Utils.encodeJSON(job.history.entries))
        }

        matcher.noMatch { HttpServerRequest r ->
            writeResource(r, "404.html")
        }

        ci.eventBus.dispatch("ci.web.setup", [router: matcher, server: server, vertx: vertx])
    }

    private void writeResource(HttpServerRequest r, String path) {
        String mimeType = MimeTypes.get(path)
        InputStream stream = getStream(path)

        r.response.headers.add("Content-Type", mimeType)

        if (stream == null) {
            writeResource(r, "404.html")
        }

        def buffer = new Buffer(stream.bytes)

        r.response.end(buffer)
    }

    private static InputStream getStream(String path) {
        File dir = new File(CI.get().configRoot, "www")
        InputStream stream
        if (!dir.exists()) {
            stream = Utils.resource("www/${path}")
        } else {
            def file = new File(dir, path)
            if (!file.exists()) {
                return null
            }
            stream = file.toPath().newInputStream()
        }
        return stream
    }
}