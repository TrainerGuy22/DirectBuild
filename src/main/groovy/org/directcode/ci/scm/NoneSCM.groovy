package org.directcode.ci.scm

import groovy.transform.CompileStatic
import org.directcode.ci.api.SCM

@CompileStatic
class NoneSCM extends SCM {

    @Override
    void execute() {
        job.buildDir.mkdirs()
    }

    @Override
    Changelog changelog(int count) {
        return new Changelog()
    }
}
