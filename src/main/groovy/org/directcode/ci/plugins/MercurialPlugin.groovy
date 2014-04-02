package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.source.MercurialSource
import org.directcode.ci.tasks.MercurialTask

class MercurialPlugin extends Plugin {
    @Override
    void apply() {
        CI.get().registerTask("mercurial", MercurialTask)
        CI.get().registerSource("mercurial", MercurialSource)
    }
}
