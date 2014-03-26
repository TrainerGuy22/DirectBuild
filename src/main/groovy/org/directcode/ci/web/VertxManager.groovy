package org.directcode.ci.web

import org.directcode.ci.core.CI
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.EventBus

/**
 * Manages Vert.x Instances
 */
class VertxManager {

    Vertx vertx = Vertx.newVertx()
    CI ci

    WebServer webServer

    VertxManager(CI ci) {
        this.ci = ci
    }

    void setupWebServer() {
        this.webServer = new WebServer(ci)
        webServer.start(ci.port, ci.host)
    }

    void stopWebServer() {
        webServer.server.close()
    }

    EventBus getEventBus() {
        return vertx.eventBus
    }

}
