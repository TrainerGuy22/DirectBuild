package org.directcode.ci.scm

import org.directcode.ci.api.SCM

class NoneSCM extends SCM {

    @Override
    void execute() {
        job.buildDir.mkdirs()
    }

    @Override
    Changelog changelog() {
        return new Changelog()
    }
}
