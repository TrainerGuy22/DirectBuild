package org.directcode.ci.core

import groovy.transform.CompileStatic

@CompileStatic
class Context {
    final CI ci

    Context(CI ci) {
        this.ci = ci
    }
}
