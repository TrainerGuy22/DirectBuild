package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.tasks.GroovyScriptTask

class GroovyScriptPlugin extends Plugin {
    @Override
    void apply(CI ci) {
        ci.registerTask("groovy", GroovyScriptTask)
    }
}
