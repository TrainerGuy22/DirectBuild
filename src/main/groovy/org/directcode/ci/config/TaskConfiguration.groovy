package org.directcode.ci.config

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.directcode.ci.api.Task
import org.directcode.ci.core.CI

@Canonical
@CompileStatic
class TaskConfiguration {
    String taskType
    Closure configClosure

    Task create() {
        return CI.get().taskTypes[taskType].getConstructor().newInstance()
    }
}
