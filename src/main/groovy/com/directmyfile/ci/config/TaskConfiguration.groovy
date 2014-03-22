package com.directmyfile.ci.config

import groovy.transform.Canonical

@Canonical
class TaskConfiguration {
    String taskType
    Closure configClosure
}
