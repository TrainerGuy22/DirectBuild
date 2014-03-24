package org.directcode.ci.jobs


import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.directcode.ci.scm.Changelog

class Job {
    private final File jobFile
    CI ci

    private JobStatus status

    private JobScript buildConfig

    Job(CI ci, File file) {
        this.ci = ci
        this.jobFile = file
        this.buildConfig = JobScript.from(file)
        buildDir.mkdirs()
    }

    def getName() {
        return buildConfig.name
    }

    List<TaskConfiguration> getTasks() {
        return buildConfig.tasks
    }

    def getBuildDir() {
        return new File(ci.configRoot, "workspace/${name}")
    }

    def getSCM() {
        return buildConfig.scm
    }

    def getArtifacts() {
        return buildConfig.artifacts
    }

    def getLogFile() {
        return new File(ci.configRoot, "logs/${name}.log")
    }

    void setStatus(JobStatus status) {
        this.status = status
        def jobInfo = ci.storage["jobs"][name] as Map<String, Object>
        jobInfo.status = status.ordinal()
    }

    JobStatus getStatus() {
        return status
    }

    void reload() {
        this.buildConfig = JobScript.from(jobFile)
    }

    void forceStatus(JobStatus status) {
        this.status = status
    }

    Changelog getChangelog() {
        def scm = ci.scmTypes[SCM.type as String].newInstance()
        scm.ci = ci
        scm.job = this
        scm.log = new JobLog(File.createTempFile("ci", "changelog"))
        return scm.changelog()
    }

    def getHistory() {
        def history = new JobHistory(this)
        history.load()
        return history
    }

    def getNotifications() {
        return buildConfig.notify
    }
}
