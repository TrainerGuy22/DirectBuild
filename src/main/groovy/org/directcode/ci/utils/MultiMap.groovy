package org.directcode.ci.utils

import groovy.transform.CompileStatic

@CompileStatic
class MultiMap<V> {
    private final Map<String, List<V>> delegate = [:]

    List<V> getAt(String key) {
        return get(key)
    }

    void putAt(String key, V value) {
        delegate[key].add(value)
    }

    List<V> get(String key) {
        if (!(key in delegate.keySet())) {
            delegate[key] = []
        }
        return delegate[key]
    }

    void add(String key, Object value) {
        this[key].add(value)
    }
}