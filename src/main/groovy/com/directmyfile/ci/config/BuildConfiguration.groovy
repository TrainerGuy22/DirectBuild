package com.directmyfile.ci.config

import com.directmyfile.ci.exception.JobConfigurationException
import com.directmyfile.ci.jobs.JobScript
import org.codehaus.groovy.control.CompilerConfiguration

class BuildConfiguration {

    private static final CompilerConfiguration compilerConfig = {
        def config = new CompilerConfiguration()
        config.scriptBaseClass = JobScript.class.name
        config
    }()

    private JobScript script
    File file

    BuildConfiguration(File file) {

        this.file = file

        if (!file.exists()) {
            throw new JobConfigurationException("No Such Job Configuration File: ${file.absolutePath}")
        }

        def shell = new GroovyShell(compilerConfig)
        script = shell.parse(file) as JobScript
        script.run()
    }

    String getName() {
        return script.name
    }

    List<TaskConfiguration> getTasks() {
        return script.tasks
    }

    def getSCM() {
        return script.scm as Map<String, Object>
    }

    def getArtifacts() {
        return script.artifacts
    }

    def getNotify() {
        return script.notify
    }

    def getRequirements() {
        return script.requirements
    }
}
