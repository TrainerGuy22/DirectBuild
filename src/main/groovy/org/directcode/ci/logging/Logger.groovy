package org.directcode.ci.logging

import groovy.transform.CompileStatic
import org.directcode.ci.core.EventBus

import java.nio.file.Path
import java.text.DateFormat
import java.text.SimpleDateFormat

@CompileStatic
class Logger {
    private static final Map<String, Logger> loggers = [:]
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.default)

    final String name
    private final EventBus eventBus = new EventBus()
    LogLevel currentLevel = LogLevel.INFO

    Logger(String name) {
        this.name = name
    }

    static void setGlobalLogLevel(LogLevel level) {
        loggers.values().each { logger ->
            logger.currentLevel = level
        }
    }

    static void logAllTo(Path path) {
        loggers.values().each { logger ->
            logger.logTo(path)
        }
    }

    static Logger getLogger(String name) {
        if (name in loggers) {
            return loggers[name]
        } else {
            return loggers[name] = new Logger(name)
        }
    }

    boolean canLog(LogLevel input) {
        return currentLevel != LogLevel.DISABLED && input == currentLevel || input == LogLevel.ERROR || input == LogLevel.INFO && currentLevel == LogLevel.DEBUG
    }

    void log(LogLevel level, String message, Throwable e = null) {
        if (canLog(level)) {
            def timestamp = dateFormat.format(new Date())
            def complete = "[${timestamp}][${name}][${level.name()}] ${message}"
            def cancelled = false
            eventBus.dispatch("log", [
                    level    : level,
                    message  : message,
                    exception: e,
                    timestamp: timestamp,
                    complete : complete,
                    cancel   : { boolean cancel = true ->
                        cancelled = cancel
                    }
            ])
            sleep(10)
            if (!cancelled) {
                println complete
                if (e) {
                    e.printStackTrace()
                }
            }
        }
    }

    void info(String message) {
        log(LogLevel.INFO, message)
    }

    void warning(String message) {
        log(LogLevel.WARNING, message)
    }

    void debug(String message) {
        log(LogLevel.DEBUG, message)
    }

    void error(String message) {
        log(LogLevel.ERROR, message)
    }

    void error(String message, Throwable e) {
        log(LogLevel.ERROR, message, e)
        e.printStackTrace()
    }

    void logTo(Path path) {
        eventBus.on('log') { Map<String, ? extends Object> event ->
            def message = event["complete"] as String
            def exception = event["exception"] as Exception
            path.append("${message}\n")
            if (exception) {
                exception.printStackTrace(path.newPrintWriter())
            }
        }
    }

    void on(String eventName, Closure handler) {
        eventBus.on(eventName, handler)
    }
}