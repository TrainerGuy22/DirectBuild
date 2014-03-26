package org.directcode.ci.logging

import groovy.transform.CompileStatic

@CompileStatic
enum LogLevel {
    ERROR, INFO, WARNING, DEBUG, DISABLED;

    static LogLevel parse(String name) {
        if (!(values()*.name().contains(name))) {
            return DISABLED
        } else {
            return values().find {
                it.name() == name
            }
        }
    }
}