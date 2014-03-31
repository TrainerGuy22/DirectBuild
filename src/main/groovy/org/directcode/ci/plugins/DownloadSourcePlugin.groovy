package org.directcode.ci.plugins

import org.directcode.ci.core.CI
import org.directcode.ci.core.plugins.Plugin
import org.directcode.ci.source.DownloadSource

class DownloadSourcePlugin extends Plugin {
    @Override
    void apply(CI ci) {
        ci.registerSource("download", DownloadSource)
    }
}
