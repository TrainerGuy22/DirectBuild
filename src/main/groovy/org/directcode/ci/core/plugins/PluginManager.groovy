package org.directcode.ci.core.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.core.CI

@CompileStatic
class PluginManager {
    CI ci
    final List<PluginProvider> providers = []

    PluginManager(CI ci) {
        this.ci = ci
        providers.add(new JarPluginProvider())
        providers.add(new ScriptPluginProvider())
        providers.add(new BundledPluginProvider())
    }

    void loadPlugins() {
        new File("plugins").mkdirs()
        providers.each { provider ->
            provider.ci = ci
            provider.loadPlugins()
        }
    }
}
