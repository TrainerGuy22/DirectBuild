package org.directcode.ci.logging

import groovy.transform.CompileStatic
import org.directcode.ci.core.EventBus
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

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

    Logger(@NotNull String name) {
        this.name = name
    }

    static void setGlobalLogLevel(@NotNull LogLevel level) {
        loggers.values().each { logger ->
            logger.currentLevel = level
        }
    }

    static void logAllTo(@NotNull Path path) {
        loggers.values().each { logger ->
            logger.logTo(path)
        }
    }

    static Logger getLogger(@NotNull String name) {
        if (name in loggers) {
            return loggers[name]
        } else {
            return loggers[name] = new Logger(name)
        }
    }

    boolean canLog(@NotNull LogLevel input) {
        return currentLevel.ordinal() >= input.ordinal()
    }

    void log(@NotNull LogLevel level, @NotNull String message, @Nullable Throwable e = null) {
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
            sleep(5)
            if (!cancelled) {
                println complete
                if (e) {
                    e.printStackTrace()
                }
            }
        }
    }

    void info(@NotNull String message) {
        log(LogLevel.INFO, message)
    }

    void warning(@NotNull String message) {
        log(LogLevel.WARNING, message)
    }

    void debug(@NotNull String message) {
        log(LogLevel.DEBUG, message)
    }

    void error(@NotNull String message, @Nullable Throwable e = null) {
        log(LogLevel.ERROR, message, e)
    }

    void logTo(@NotNull Path path) {
        eventBus.on('log') { Map<String, ? extends Object> event ->
            def message = event["complete"] as String
            def exception = event["exception"] as Exception
            path.append("${message}\n")
            if (exception) {
                exception.printStackTrace(path.newPrintWriter())
            }
        }
    }

    void on(@NotNull String eventName, @NotNull Closure handler) {
        eventBus.on(eventName, handler)
    }
}