package org.directcode.ci.tasks

import org.codehaus.groovy.control.CompilerConfiguration
import org.directcode.ci.api.Task

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
