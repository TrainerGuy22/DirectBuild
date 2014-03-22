package org.directcode.ci.jobs

import org.directcode.ci.config.ArtifactSpec
import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.directcode.ci.exception.JobConfigurationException

abstract class JobScript extends Script {
    String name = this.class.simpleName
    List<TaskConfiguration> tasks = []
    ArtifactSpec artifacts = new ArtifactSpec()
    Map<String, Object> notify = [:]
    Map<String, Object> scm = [:]
    List<String> requirements = []

    void name(String name) {
        this.name = name
    }

    void require(String name) {
        requirements.add(name)
    }

    void task(String type, Closure closure) {
        if (type in CI.instance.taskTypes.keySet()) {
            def config = new TaskConfiguration()
            config.taskType = type
            config.configClosure = closure
            tasks.add(config)
        } else {
            throw new JobConfigurationException("Task of type '${type}' does not exist!")
        }
    }

    void tasks(Closure closure) {
        new TaskContainer().with(closure)
    }

    void scm(Map<String, Object> opts) {
        scm = opts
    }

    void artifact(String location) {
        artifacts.file(location)
    }

    void artifacts(Closure closure) {
        artifacts.with(closure)
    }

    class TaskContainer {
        @Override
        Object invokeMethod(String name, Object args) {
            def argz = args as Object[]
            def firstArg = argz[0] as Closure

            task(name, firstArg)

            return null
        }
    }
}