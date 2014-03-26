package org.directcode.ci.jobs


import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.directcode.ci.scm.Changelog

class Job {
    private final File jobFile
    CI ci

    private JobStatus status

    private JobScript buildConfig

    final WebHooks webHooks = new WebHooks(this)

    Job(CI ci, File file) {
        this.ci = ci
        this.jobFile = file
        this.buildConfig = JobScript.from(file, this)
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
        forceStatus(status)
        def jobInfo = ci.storage["jobs"][name] as Map<String, Object>
        jobInfo.status = status.ordinal()
        ci.storage.save("jobs")
    }

    JobStatus getStatus() {
        return status
    }

    void reload() {
        this.buildConfig = JobScript.from(jobFile, this)
    }

    void forceStatus(JobStatus status) {
        this.status = status
    }

    Changelog getChangelog(int count = 4) {
        def scm = ci.scmTypes[SCM.type as String].newInstance()
        scm.ci = ci
        scm.job = this
        scm.log = new JobLog(File.createTempFile("simpleci", "changelog"))
        return scm.changelog(count)
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
