package org.directcode.ci.jobs

import org.directcode.ci.config.BuildConfiguration
import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.directcode.ci.scm.Changelog

class Job {
    BuildConfiguration buildConfig
    CI ci

    private JobStatus status

    int id

    Job(CI ci, File file) {
        this.ci = ci
        this.buildConfig = new BuildConfiguration(file)
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
        return buildConfig.SCM
    }

    def getArtifacts() {
        return buildConfig.artifacts
    }

    def getLogFile() {
        return new File(ci.configRoot, "logs/${name}.log")
    }

    void setStatus(JobStatus status) {
        this.status = status
        ci.sql.update("UPDATE `jobs` SET  `status` =  '${status.ordinal()}' WHERE  `jobs`.`id` = ${id};")
    }

    JobStatus getStatus() {
        return status
    }

    void reload() {
        this.buildConfig = new BuildConfiguration(buildConfig.file)

        this.status = JobStatus.parse(ci.sql.firstRow("SELECT `status` FROM `jobs` WHERE `id` = ${id};").status as int)
    }

    void forceStatus(JobStatus status) {
        this.status = status
    }

    Changelog getChangelog() {
        return ci.scmTypes[SCM.type as String].changelog(this)
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
