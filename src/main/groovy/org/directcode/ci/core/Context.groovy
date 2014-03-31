package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
class Context {
    final CI ci

    Context(@NotNull CI ci) {
        this.ci = ci
    }
}
