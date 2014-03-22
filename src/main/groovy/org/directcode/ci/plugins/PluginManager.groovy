package org.directcode.ci.plugins

import org.directcode.ci.core.CI

class PluginManager {
    CI ci
    final List<PluginProvider> providers = []

    PluginManager(CI ci) {
        this.ci = ci
        providers.add(new JarPluginProvider())
        providers.add(new ScriptPluginProvider())
    }

    void loadPlugins() {
        new File("plugins").mkdirs()
        providers.each { provider ->
            provider.ci = ci
            provider.loadPlugins()
        }
    }
}
