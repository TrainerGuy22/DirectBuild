package org.directcode.ci.utils

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.nio.file.Path

@SuppressWarnings("GroovyMissingReturnStatement")
class ExecuteSpec {
    Closure streamOutput = null
    String executable
    List<String> args = []
    File directory = new File(".")
    Map<String, String> environment = {
        def env = [:]
        env.putAll(System.getenv())
        env
    }()

    void directory(File directory) {
        this.directory = directory
    }

    void directory(Path path) {
        this.directory = path.toFile()
    }

    void directory(String path) {
        this.directory = new File(path)
    }

    void argument(String arg) {
        args << arg
    }

    void args(List<String> args) {
        this.args.addAll(args)
    }

    void arguments(List<String> arguments) {
        this.args.addAll(arguments)
    }

    void executable(String executable) {
        this.executable = executable
    }

    void executable(File path) {
        this.executable = path.absolutePath
    }

    void executable(Path path) {
        this.executable = path.toFile().absolutePath
    }

    void variable(String name, String value) {
        environment[name] = value
    }

    void environment(Map<String, String> env) {
        environment.putAll(env)
    }

    void streamOutput(@ClosureParams(value = SimpleType, options = "java.lang.String") Closure closure) {
        this.streamOutput = closure
    }

    ExecuteResult execute() {
        def process = "${executable} ${args.join(" ")}".execute(convertToList(environment), directory)
        def lines = []
        Thread.startDaemon("ExecuteProcessErrorStream") { ->
            process.errorStream.eachLine { line ->
                lines.add(line)
                if (streamOutput) {
                    streamOutput(line)
                }
            }
        }
        process.inputStream.eachLine { line ->
            lines.add(line)
            if (streamOutput) {
                streamOutput(line)
            }
        }
        def code = process.waitFor()
        process.closeStreams()
        return new ExecuteResult(code, lines)
    }

    private static List<String> convertToList(Map<String, String> input) {
        def output = []
        input.each { entry ->
            output.add("${entry.key}=${entry.value}")
        }
        return output
    }
}
