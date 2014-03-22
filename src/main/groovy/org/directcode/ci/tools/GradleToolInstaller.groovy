package org.directcode.ci.tools

import org.directcode.ci.api.ToolInstaller

class GradleToolInstaller extends ToolInstaller {
    @Override
    boolean install() {
        return false
    }

    @Override
    boolean remove() {
        return false
    }
}
