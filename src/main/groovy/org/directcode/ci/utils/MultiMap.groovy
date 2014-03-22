package org.directcode.ci.utils

class MultiMap<V> {
    private final Map<String, List<V>> delegate = [:]

    List<V> getAt(String key) {
        return get(key)
    }

    void putAt(String key, Object value) {
        delegate[key] = value
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