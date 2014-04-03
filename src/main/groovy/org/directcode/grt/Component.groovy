package org.directcode.grt

import groovy.xml.DOMBuilder

class Component {
    protected Closure build

    void build(@DelegatesTo(DOMBuilder) Closure closure) {
        build = closure
    }
}
