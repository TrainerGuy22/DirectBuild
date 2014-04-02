package org.directcode.ci.web

import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher

abstract class DCScript extends Script {
    protected DataType type
    protected Closure creator
    protected RouteMatcher router

    void type(DataType type) {
        this.type = type
    }

    void create(Closure creator) {
        this.creator = creator
    }

    void mapping(Closure mapping) {
        mapping.delegate = new Object() {
            void get(String path) {
                router.get(path) { HttpServerRequest request ->
                    type.handle(request, { req, builder ->
                        creator(req, builder)
                    })
                }
            }
        }
    }
}
