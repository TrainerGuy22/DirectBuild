package com.directmyfile.ci.config

import com.directmyfile.ci.exception.JobConfigurationException
import groovy.json.JsonSlurper
import com.directmyfile.ci.jobs.JobScript
import org.codehaus.groovy.control.CompilerConfiguration

class BuildConfig {

    private static final CompilerConfiguration compilerConfig = {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = JobScript.class.name
        return config
    }()

    private final JobScript script
    File file

    BuildConfig(File file) {

        this.file = file

        if (!file.exists()) {
            throw new JobConfigurationException("No Such Job Configuration File: ${file.absolutePath}")
        }

        def shell = new GroovyShell(compilerConfig)
        script = shell.parse(file)
        script.run()
    }

    String getName() {
        return script.name
    }

    List<Map<String, Object>> getTasks() {
        return script.tasks as List<Object>
    }

    def getSCM() {
        return script.scm as Map<String, Object>
    }

    def getArtifacts() {
        return script.artifacts as List<String>
    }

    def getNotify() {
        return script.notify
    }

    def getRequirements() {
        return script.requirements
    }
}
