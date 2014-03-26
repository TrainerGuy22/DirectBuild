package org.directcode.ci.config

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class TaskConfiguration {
    String taskType
    Closure configClosure
}
