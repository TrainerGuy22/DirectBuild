package org.directcode.ci.config

import groovy.transform.CompileStatic

@CompileStatic
class CiConfig extends GConfig {
    CiConfig() {
        defaultConfig = this.class.classLoader.getResourceAsStream("defaultConfig.groovy").text
    }

    Map<String, Object> ciSection() {
        return getProperty("ci", [
                builders: 4
        ]) as Map<String, Object>
    }

    Map<String, List<String>> pluginsSection() {
        return getProperty("plugins", [
                disabled: []
        ]) as Map<String, List<String>>
    }

    Map<String, Object> loggingSection() {
        return getProperty("logging", [
                level: "INFO"
        ]) as Map<String, Object>
    }

    Map<String, String> pathsSection() {
        return getProperty("paths", [:]) as Map<String, String>
    }

    Map<String, Object> webSection() {
        return getProperty("web", [
                host: "0.0.0.0",
                port: 8080
        ]) as Map<String, Object>
    }
}
