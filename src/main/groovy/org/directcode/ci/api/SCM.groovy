package org.directcode.ci.api

import org.directcode.ci.core.CI
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog
import org.directcode.ci.scm.Changelog

/**
 * A Source Code Manager
 */
abstract class SCM {

    CI ci
    Job job
    JobLog log

    abstract void execute();

    /**
     * Makes Changelog from SCM
     * @return SCM Changelog
     */
    abstract Changelog changelog();
}
