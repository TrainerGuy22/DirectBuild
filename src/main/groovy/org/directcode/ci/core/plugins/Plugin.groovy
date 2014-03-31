package org.directcode.ci.core.plugins

import org.directcode.ci.core.CI

abstract class Plugin {
    abstract void apply(CI ci);
}
