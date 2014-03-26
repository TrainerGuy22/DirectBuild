package org.directcode.ci.web

import groovy.transform.CompileStatic

@CompileStatic
class MimeTypes {
    static Map<String, List<String>> types = [
            "font/x-woff"           : [".woff"],
            "text/html"             : [".html", ".htm"],
            "application/json"      : [".json"],
            "application/javascript": [".js"],
            "text/css"              : [".css"],
            "image/*"               : [".png", ".jpeg"]
    ]

    static String get(String fileName) {
        def extension

        def split = fileName.tokenize('.')

        if (split.size() == 1) {
            extension = ""
        } else {
            extension = ".${split.last()}"
        }

        def type = "text/plain"

        types.keySet().each { key ->
            List<String> value = types[key]
            if (value.contains(extension)) {
                type = key
            }
        }

        return type
    }
}
