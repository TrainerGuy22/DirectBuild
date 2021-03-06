package org.directcode.ci.logging

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
enum LogLevel {
    DISABLED, ERROR, WARNING, INFO, DEBUG;

    static LogLevel parse(@NotNull String name) {
        if (!(values()*.name().contains(name))) {
            return DISABLED
        } else {
            return values().find {
                it.name() == name
            }
        }
    }
}