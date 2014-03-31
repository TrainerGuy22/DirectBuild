package org.directcode.ci.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI

/**
 * Plugin Provider for loading bundled plugins
 */
@CompileStatic
class BundledPluginProvider extends PluginProvider {
    static Set<Class<? extends Plugin<CI>>> bundledPlugins = []

    @Override
    void loadPlugins() {
        for (plugin in bundledPlugins) {
            plugin.getConstructor().newInstance().apply(ci)
        }
    }
}
