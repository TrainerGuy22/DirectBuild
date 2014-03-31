package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.source.GitSource
import org.directcode.ci.tasks.GitTask

class GitPlugin extends Plugin {
    @Override
    void apply(CI ci) {
        ci.registerTask("git", GitTask)
        ci.registerSource("git", GitSource)
    }
}
