package org.directcode.ci.utils

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class ConsoleHandler {
    static BufferedReader reader = System.in.newReader()
    static boolean looping = false

    static void readLine(@ClosureParams(value = FromString, options = "String") Closure handler = {}) {
        def line = reader.readLine()
        if (line == null || line.trim() == "") {
            return
        }
        def split = line.tokenize()
        def cmd = split[0]
        def args = split.drop(1)
        handler(cmd, args)
    }

    static void loop(@ClosureParams(value = FromString, options = ["String", "List<String>"]) Closure handler = {
    }, Closure<Boolean> stopHandler = { false }) {
        if (looping) {
            throw new IllegalStateException("Console is already looping.")
        }
        // Asynchronous Loop
        Thread.start("Console") { ->
            looping = true
            while (!stopHandler()) {
                readLine(handler)
            }
            looping = false
        }
    }

    static void readLines(@ClosureParams(value = FromString, options = "List<String>") Closure handler = {}) {
        handler(reader.readLines())
    }
}