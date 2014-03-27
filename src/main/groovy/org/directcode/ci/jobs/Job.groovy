package org.directcode.ci.jobs

import org.directcode.ci.config.ArtifactSpec
import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.directcode.ci.scm.Changelog

class Job {
    private final File jobFile
    CI ci

    private JobStatus status

    private JobScript buildConfig

    final WebHooks webHooks

    Job(CI ci, File file) {
        this.ci = ci
        this.webHooks = new WebHooks(this)
        this.jobFile = file
        this.buildConfig = JobScript.from(file, this)
        buildDir.mkdirs()
    }

    String getName() {
        return buildConfig.name
    }

    List<TaskConfiguration> getTasks() {
        return buildConfig.tasks
    }

    File getBuildDir() {
        return new File(ci.configRoot, "workspace/${name}")
    }

    Map<String, Object> getSCM() {
        return buildConfig.scm
    }

    ArtifactSpec getArtifacts() {
        return buildConfig.artifacts
    }

    File getLogFile() {
        return new File(ci.configRoot, "logs/${name}.log")
    }

    void setStatus(JobStatus status) {
        forceStatus(status)
        def jobInfo = ci.storage["jobs"][name] as Map<String, Object>
        jobInfo.status = status.ordinal()
        ci.storage.save("jobs")
        ci.logger.debug("Job '${name}': Status updated to '${status}'")
    }

    JobStatus getStatus() {
        return status
    }

    void reload() {
        this.buildConfig = JobScript.from(jobFile, this)
    }

    void forceStatus(JobStatus status) {
        this.@status = status
    }

    Changelog getChangelog(int count = 4) {
        def scm = ci.scmTypes[SCM.type as String].newInstance()
        scm.ci = ci
        scm.job = this
        scm.log = new JobLog(File.createTempFile("simpleci", "changelog"))
        return scm.changelog(count)
    }

    JobHistory getHistory() {
        def history = new JobHistory(this)
        history.load()
        return history
    }

    Map<String, Object> getNotifications() {
        return buildConfig.notify
    }
}
