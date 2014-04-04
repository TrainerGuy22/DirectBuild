package org.directcode.ci.web

import org.directcode.ci.core.CI
import org.directcode.grt.TemplateFactory
import org.intellij.lang.annotations.Language

class BaseComponents {
    static void load(TemplateFactory factory) {
        factory.define("jquery") { opts ->
            build {
                script(src: "/js/jquery.min.js")
            }
        }

        factory.define("bootstrap") { opts ->
            build {
                link(rel: "stylesheet", href: "/css/bootstrap.min.css")
                script(src: "/js/bootstrap.min.js")
                style("""
                body {
                    padding-top: 60px;
                }
                """.stripIndent())
            }
        }

        factory.define("navigation") { opts ->
            def navbar = (List<Map<String, ? extends Object>>) [
                    [
                            name: "Home",
                            path: "/"
                    ],
                    [
                            name: "Jobs",
                            path: "/jobs"
                    ]
            ]
            if (opts.pages)
                navbar.addAll((List<Map<String, ? extends Object>>) opts.pages)
            build {
                nav(class: "navbar navbar-default navbar-fixed-top", role: "navigation") {
                    div(class: "navbar-header") {
                        a(class: "navbar-brand", href: "/", "DirectBuild")
                    }
                    ul(class: "nav navbar-nav", id: "navigate") {
                        navbar.each { nav ->
                            if (nav.name == opts.active) {
                                li(class: "active") {
                                    a(href: nav.path, nav.name)
                                }
                            } else {
                                li {
                                    a(href: nav.path, nav.name)
                                }
                            }
                        }
                    }
                }
            }
        }

        factory.define("job_table") { opts ->
            build {
                table(class: "job-table table table-bordered", border: "1") {
                    thead {
                        tr {
                            th("Name")
                            th("Status")
                        }
                    }
                    tbody(id: "jobList") {
                        CI.get().jobs.values().each { job ->
                            tr(class: job.status.contextClass, id: "job-${job.name}") {
                                td {
                                    a(href: "/job/${job.name}", job.name)
                                }
                                td(job.status.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
