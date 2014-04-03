package org.directcode.grt

import groovy.text.SimpleTemplateEngine
import groovy.text.TemplateEngine
import groovy.xml.MarkupBuilder

class TemplateFactory {
    private final TemplateEngine templateEngine

    final Map<String, Closure> components

    TemplateFactory() {
        components = [:]
        templateEngine = new SimpleTemplateEngine()
    }

    GrtTemplate create(Reader reader) {
        return new GrtTemplate(this, templateEngine.createTemplate(reader))
    }

    void component(String name, @DelegatesTo(Component) Closure closure) {
        components[name] = closure
    }

    protected String useComponent(String name, Map<String, ? extends Object> opts) {
        def component = components[name]
        if (component == null) {
            throw new IllegalArgumentException("Component '${name}' does not exist")
        }
        def c = new Component()
        component.delegate = c
        component()
        def builder = new MarkupBuilder()
        builder.doubleQuotes = true

        c.build.delegate = builder
        c.build.call(opts)
        return builder
    }
}
