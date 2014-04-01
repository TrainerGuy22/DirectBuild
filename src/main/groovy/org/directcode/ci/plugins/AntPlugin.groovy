package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.tasks.AntTask

class AntPlugin extends Plugin {
    @Override
    void apply() {
        CI.get().registerTask("ant", AntTask)
    }
}
