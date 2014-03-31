package org.directcode.ci.core.plugins

import groovy.transform.CompileStatic
import plugins.GradlePlugin

/**
 * Plugin Provider for loading bundled plugins
 */
@CompileStatic
class BundledPluginProvider extends PluginProvider {
    static List<Class<? extends Plugin>> bundledPlugins = [
            GradlePlugin
    ]

    @Override
    void loadPlugins() {
        for (plugin in bundledPlugins) {
            plugin.getConstructor().newInstance().apply(ci)
        }
    }
}
