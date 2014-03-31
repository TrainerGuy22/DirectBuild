package org.directcode.ci.core.plugins

import groovy.transform.CompileStatic
import org.directcode.ci.utils.FileMatcher

import javax.script.ScriptEngineManager

/**
 * A Plugin Provider that uses the Java Scripting API to provide for all types of scripts.
 */
@CompileStatic
class ScriptPluginProvider extends PluginProvider {
    @Override
    void loadPlugins() {
        def manager = new ScriptEngineManager()
        manager.engineFactories.each { factory ->
            def engine = factory.scriptEngine
            engine.put("ci", ci)
            engine.put("logger", ci.logger)
            FileMatcher.create(new File(ci.configRoot, "org.directcode.ci.plugins")).withExtensions(factory.extensions) { File file ->
                engine.eval(file.newReader())
            }
        }
    }
}
