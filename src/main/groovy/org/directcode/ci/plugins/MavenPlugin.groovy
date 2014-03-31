package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.tasks.MavenTask

class MavenPlugin extends Plugin {
    @Override
    void apply(CI ci) {
        ci.registerTask("maven", MavenTask)
    }
}
