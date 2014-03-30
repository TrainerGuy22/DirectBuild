package org.directcode.ci.web

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.directcode.ci.core.CI

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

    @Memoized(maxCacheSize = 50)
    static String get(String fileName) {
        def extension

        def split = fileName.split("\\.")

        if (split.size() == 1) {
            extension = ""
        } else {
            extension = ".${split.last()}"
        }

        def type = "text/plain"

        types.keySet().each { key ->
            List<String> value = types[key]
            if (value.findAll {
                it == extension
            }.size() != 0) {
                type = key
            }
        }

        CI.logger.debug("'${fileName}' determined to have the mimetype '${type}'")

        return type
    }
}
