package org.directcode.grt

import groovy.text.Template

class GrtTemplate {
    private TemplateFactory factory
    private Template template

    GrtTemplate(TemplateFactory factory, Template template) {
        this.factory = factory
        this.template = template
    }

    Writable make(Map<String, ? extends Object> binding) {
        def realBinding = [
                component: { String name, Map<String, ? extends Object> opts ->
                    return factory.useComponent(name, opts)
                }
        ] as Map<String, ? extends Object>
        realBinding.putAll(binding)
        return template.make(binding)
    }
}
