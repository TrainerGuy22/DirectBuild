package org.directcode.ci.plugins

import org.directcode.ci.core.CI

/**
 * A Plugin Provider will allow extending the plugin system to include multiple types of plugins.
 */
abstract class PluginProvider {
    CI ci

    abstract void loadPlugins();
}
