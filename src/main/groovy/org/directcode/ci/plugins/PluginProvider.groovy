package org.directcode.ci.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI

/**
 * A Plugin Provider will allow extending the plugin system to include multiple types of plugins.
 */
@CompileStatic
abstract class PluginProvider {
    CI ci

    abstract void loadPlugins();
}
