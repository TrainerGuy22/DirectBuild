package com.directmyfile.ci.tasks

import com.directmyfile.ci.api.Task
import org.codehaus.groovy.control.CompilerConfiguration

class GroovyScriptTask extends Task {
    String script

    void execute() {

        def compiler = new CompilerConfiguration()

        compiler.output = log.out

        def shell = new GroovyShell(compiler)

        try {
            def theScript = shell.parse(script)
            theScript.run()
        } catch (e) {
            e.printStackTrace(compiler.output)
        }
    }

    @Override
    void configure(Closure closure) {
        with(closure)
    }
}
