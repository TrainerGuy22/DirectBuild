package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.directcode.ci.api.Source
import org.directcode.ci.api.Task
import org.directcode.ci.exception.CIException
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog
import org.directcode.ci.jobs.JobStatus
import org.directcode.ci.logging.Logger
import org.directcode.ci.utils.ExecutionTimer
import org.directcode.ci.utils.Utils

@CompileStatic
class Build {

    static final Logger logger = Logger.getLogger("Builder")

    final Job job
    final int number
    private boolean complete
    private boolean waiting

    Build(Job job, int number) {
        this.job = job
        this.number = number
        this.complete = false
        this.waiting = true
    }

    protected void execute() {
        def ci = CI.get()
        this.waiting = false
        def eventBus = ci.eventBus

        eventBus.dispatch("ci.build.started", [
                jobName: job.name,
                number : number,
                build  : this
        ])

        def timer = new ExecutionTimer()

        timer.start()

        def success = true
        def scmShouldRun = true
        def tasksShouldRun = true

        job.status = JobStatus.RUNNING
        logger.info "Build '${job.name}:${number}' is building"

        if (!job.buildDir.exists()) {
            job.buildDir.mkdirs()
        }

        def jobLog = new JobLog(job.logFile)

        if (scmShouldRun) {
            def scmConfig = job.source

            if (!ci.sourceTypes.containsKey(scmConfig.type)) {
                logger.error "Build '${job.name}:${number}' is attempting to use a non-existant Source Type '${scmConfig.type}!'"
                success = false
                tasksShouldRun = false
            } else {
                Source source = ((Class<? extends Source>) ci.sourceTypes[scmConfig.type as String]).getConstructor().newInstance()

                source.job = job
                source.log = jobLog

                try {
                    source.execute()
                } catch (CIException e) {
                    logger.info "Job '${job.name}' (Source): ${e.message}"
                    tasksShouldRun = false
                    success = false
                }
            }
        }

        if (tasksShouldRun) {
            def tasks = job.tasks

            for (taskConfig in tasks) {
                def id = tasks.indexOf(taskConfig) + 1
                logger.info "Running Task ${id} of ${job.tasks.size()} for Build '${job.name}:${number}'"

                if (!(taskConfig.taskType in ci.taskTypes.keySet())) {
                    logger.error("Build '${job.name}:${number}': Unknown task type '${taskConfig.taskType}'")
                    success = false
                    break
                }

                Task task = taskConfig.create()

                task.job = job
                task.build = this
                task.log = jobLog

                task.configure(taskConfig.configClosure)

                try {
                    task.execute()
                } catch (e) {
                    logger.error("Build '${job.name}:${number}' (Task #${id}): ${e.message}")
                    success = false
                    break
                }
            }

            def artifactsDir = new File(ci.artifactDir, "${job.name}/${number}").absoluteFile
            artifactsDir.mkdirs()
            job.artifacts.files.each { location ->
                def source = new File(job.buildDir, location)
                def target = new File(artifactsDir, source.name)
                if (!source.exists()) {
                    jobLog.write("Artifact '${location}' does not exist. Skipping.")
                    return
                }
                target.bytes = source.bytes
            }
        }

        def buildTime = timer.stop()

        logger.debug "Build '${job.name}:${number}' completed in ${buildTime} milliseconds"

        if (!success) {
            logger.info "Build '${job.name}:${number}' has Failed"
            job.status = JobStatus.FAILURE
        } else {
            logger.info "Build '${job.name}:${number}' has Completed"
            job.status = JobStatus.SUCCESS
        }

        eventBus.dispatch("ci.build.done", [
                jobName   : job.name,
                status    : job.status,
                buildTime : buildTime,
                timeString: timer.toString(),
                number    : number,
                build     : this
        ])

        jobLog.complete()

        def log = job.logFile.text

        def base64Log = Utils.encodeBase64(log)

        def job_history = ci.storage.get("job_history")

        def history = ((List<Map<String, ? extends Object>>) job_history.get(job.name, new LinkedList<>()))

        history.add([number: number, status: job.status.ordinal(), log: base64Log, buildTime: buildTime, timeStamp: new Date().toString()])
        this.complete = true
    }

    boolean isComplete() {
        return complete
    }

    boolean isWaiting() {
        return waiting
    }

    boolean isRunning() {
        return !waiting && !complete
    }

    void waitFor() {
        while (waiting || running) {
            sleep(5)
        }
    }
}
