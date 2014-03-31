package plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.tasks.GradleTask

class GradlePlugin extends Plugin {
    @Override
    void apply(CI ci) {
        ci.registerTask("gradle", GradleTask)
    }
}
