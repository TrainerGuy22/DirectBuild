package org.directcode.ci.core.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.plugins.GitPlugin
import org.directcode.ci.plugins.GradlePlugin

/**
 * Plugin Provider for loading bundled org.directcode.ci.plugins
 */
@CompileStatic
class BundledPluginProvider extends PluginProvider {
    static List<Class<? extends Plugin>> bundledPlugins = [
            GradlePlugin,
            GitPlugin
    ]

    @Override
    void loadPlugins() {
        for (plugin in bundledPlugins) {
            plugin.getConstructor().newInstance().apply(ci)
        }
    }
}
