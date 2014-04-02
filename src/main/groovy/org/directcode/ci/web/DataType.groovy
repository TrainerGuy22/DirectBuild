package org.directcode.ci.web

import groovy.json.JsonBuilder
import groovy.xml.MarkupBuilder
import org.vertx.groovy.core.http.HttpServerRequest

interface DataType {

    static final DataType JSON = new DataType() {
        @Override
        void handle(HttpServerRequest request, Closure closure) {
            def prettyPrint = (request.params["prettyPrint"] as String).empty ?: false
            def builder = new JsonBuilder()
            closure(request, builder)
            request.response.end(prettyPrint ? builder.toPrettyString() : builder.toString())
        }
    }

    static final DataType XML = new DataType() {
        @Override
        void handle(HttpServerRequest request, Closure closure) {
            def prettyPrint = (request.params["prettyPrint"] as String).empty ?: false
            def builder = new MarkupBuilder()
            closure(request, builder)
            request.response.end(prettyPrint ? builder.toPrettyString() : builder.toString())
        }
    }

    void handle(HttpServerRequest request, Closure closure);
}