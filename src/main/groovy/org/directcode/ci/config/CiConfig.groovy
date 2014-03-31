package org.directcode.ci.config

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI

@CompileStatic
class CiConfig extends GConfig {
    private final CI ci

    CiConfig(CI ci) {
        super(new File(ci.configRoot, "config.groovy"))
        this.ci = ci

        defaultConfig = this.class.classLoader.getResourceAsStream("defaultConfig.groovy").text
    }

    @Override
    void load() {
        super.load()

        def web = getProperty("web", [
                host: "0.0.0.0",
                port: 8080
        ]) as Map<String, Object>

        ci.host = web['host'] as String
        ci.port = web['port'] as int
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

    Map<String, Object> securitySection() {
        return getProperty("security", [
                enabled: false
        ]) as Map<String, Object>
    }

    Map<String, String> pathsSection() {
        return getProperty("paths", [:]) as Map<String, String>
    }
}
