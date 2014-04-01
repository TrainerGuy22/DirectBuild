package org.directcode.ci.jobs

import org.directcode.ci.config.ArtifactSpec
import org.directcode.ci.config.TaskConfiguration
import org.directcode.ci.core.CI
import org.jetbrains.annotations.NotNull

class Job {
    private final File jobFile

    private JobStatus status

    private JobScript buildConfig

    final WebHooks webHooks

    Job(@NotNull File file) {
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
        return new File(CI.get().configRoot, "workspace/${name}").absoluteFile
    }

    Map<String, ? extends Object> getSource() {
        return buildConfig.source
    }

    ArtifactSpec getArtifacts() {
        return buildConfig.artifacts
    }

    File getLogFile() {
        return new File(CI.get().configRoot, "logs/${name}.log").absoluteFile
    }

    void setStatus(@NotNull JobStatus status) {
        forceStatus(status)
        def jobInfo = CI.get().storage["jobs"][name] as Map<String, Object>
        jobInfo.status = status.ordinal()
        CI.get().storage.save("jobs")
        CI.get().logger.debug("Job '${name}': Status updated to '${status}'")
    }

    JobStatus getStatus() {
        return status
    }

    void reload() {
        this.buildConfig = JobScript.from(jobFile, this)
    }

    void forceStatus(@NotNull JobStatus status) {
        this.@status = status
    }

    JobHistory getHistory() {
        def history = new JobHistory(this)
        history.load()
        return history
    }
}
