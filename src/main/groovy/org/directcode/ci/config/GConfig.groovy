package org.directcode.ci.config

import groovy.transform.CompileStatic
import org.directcode.ci.utils.Utils

@CompileStatic
class GConfig {
    private final File configFile
    private Binding config
    private String defaultConfig

    GConfig(File configFile) {
        this.configFile = configFile
    }

    void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig
    }

    void load() {
        if (!configFile.exists()) {
            configFile.write(defaultConfig)
        }
        def configScript = Utils.parseConfig(configFile)

        configScript.run()

        this.config = configScript.binding
    }

    Object getProperty(String key) {
        if (metaClass.hasProperty(key)) {
            return metaClass.getProperty(this, key)
        } else {
            return config.getVariable(key)
        }
    }

    Object getProperty(String key, Object defaultValue) {
        if (!hasProperty(key)) {
            return defaultValue
        } else {
            return getProperty(key)
        }
    }

    void setProperty(String key, Object value) {
        if (metaClass.hasProperty(this, key)) {
            metaClass.setProperty(this, key, value)
        } else {
            this.config.setVariable(key, value)
        }
    }

    boolean hasProperty(String key) {
        return metaClass.hasProperty(this, key) || config.hasVariable(key)
    }
}