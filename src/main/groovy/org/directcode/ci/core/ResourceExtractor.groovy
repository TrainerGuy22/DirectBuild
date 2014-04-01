package org.directcode.ci.core

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.jetbrains.annotations.NotNull

import java.util.jar.JarEntry
import java.util.jar.JarFile

@CompileStatic
class ResourceExtractor {
    @Memoized
    static File currentJar() {
        def jarFile = new File(this.protectionDomain.codeSource.location.toURI())
        if (!jarFile.name.endsWith(".jar")) {
            return null
        } else {
            return jarFile
        }
    }

    static void extractWWW(@NotNull File destination) {
        if (currentJar() == null) {
            return
        }
        destination.deleteDir()
        destination.mkdirs()
        destination.deleteOnExit()
        def jar = new JarFile(currentJar())
        jar.entries().each { JarEntry entry ->
            if (entry.name.startsWith("www/")) {
                def file = new File(destination, entry.name)
                file.parentFile.mkdirs()
                file.toPath().newOutputStream() << jar.getInputStream(entry)
            }
        }
    }
}
