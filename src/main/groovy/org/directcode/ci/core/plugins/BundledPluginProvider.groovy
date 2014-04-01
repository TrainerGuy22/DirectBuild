package org.directcode.ci.core.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI
import org.directcode.ci.plugins.*

/**
 * Plugin Provider for loading bundled org.directcode.ci.plugins
 */
@CompileStatic
class BundledPluginProvider extends PluginProvider {
    static final List<Class<? extends Plugin>> bundledPlugins = [
            GradlePlugin,
            GitPlugin,
            MakePlugin,
            AntPlugin,
            MavenPlugin,
            GroovyScriptPlugin,
            DownloadSourcePlugin
    ]

    @Override
    void loadPlugins() {
        for (plugin in bundledPlugins) {
            if (CI.get().config.pluginsSection()["disabled"]?.contains(plugin.name)) {
                return
            }
            plugin.getConstructor().newInstance().apply()
        }
    }
}
