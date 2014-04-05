package org.directcode.ci.core

import groovy.transform.Canonical

@Canonical
class BuildDescriptor {
    String jobName
    int number
}
