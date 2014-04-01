package org.directcode.ci.jobs

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
enum JobStatus {
    SUCCESS, FAILURE, NOT_STARTED, RUNNING, WAITING;

    @Override
    String toString() {
        return this.name().toLowerCase().capitalize().replace('_', ' ')
    }

    static JobStatus parse(@NotNull int id) {
        if (id < 0 || id >= values().size()) {
            return NOT_STARTED
        }
        return values()[id]
    }
}