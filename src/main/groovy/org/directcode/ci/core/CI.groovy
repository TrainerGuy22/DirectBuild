package org.directcode.ci.core
import org.directcode.ci.api.SCM
import org.directcode.ci.api.Task
import org.directcode.ci.config.CiConfig
import org.directcode.ci.exception.CIException
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobLog
import org.directcode.ci.jobs.JobStatus
import org.directcode.ci.logging.LogLevel
import org.directcode.ci.logging.Logger
import org.directcode.ci.plugins.PluginManager
import org.directcode.ci.scm.GitSCM
import org.directcode.ci.scm.NoneSCM
import org.directcode.ci.tasks.*
import org.directcode.ci.utils.ExecutionTimer
import org.directcode.ci.utils.FileMatcher
import org.directcode.ci.utils.Utils
import org.directcode.ci.web.VertxManager

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class CI {

    private static CI INSTANCE

    /**
     * Main CI Logger
     */
    static final Logger logger = Logger.getLogger("CI")

    /**
     * Configuration Root
     */
    File configRoot = new File(".")

    /**
     * CI Server Web Port
     */
    int port = 0

    /**
     * CI Server Web Host
     */
    String host = "0.0.0.0"

    /**
     * Plugin Manager
     */
    final PluginManager pluginManager = new PluginManager(this)

    /**
     * CI Configuration
     */
    final CiConfig config = new CiConfig(this)

    /**
     * CI Storage System
     */
    final CIStorage storage = new CIStorage()

    /**
     * CI Task Types
     */
    final Map<String, Class<? extends Task>> taskTypes = [
            command: CommandTask,
            gradle : GradleTask,
            make   : MakeTask,
            git    : GitTask,
            groovy : GroovyScriptTask
    ]

    /**
     * Source Code Manager Types
     */
    final Map<String, Class<? extends SCM>> scmTypes = [
            git: GitSCM,
            none: NoneSCM
    ]

    /**
     * CI Jobs
     */
    final Map<String, Job> jobs = [:]

    /**
     * Job Queue System
     */
    BlockingQueue<Job> jobQueue

    /**
     * Vert.x Manager for managing Vert.x related systems
     */
    final VertxManager vertxManager = new VertxManager(this)

    /**
     * CI Event Bus
     */
    final EventBus eventBus = new EventBus()

    /**
     * Starts CI Server
     */
    void start() {
        init()
        loadJobs()
        vertxManager.setupWebServer()
    }

    /**
     * Initializes this CI Server
     */
    private void init() {
        def timer = new ExecutionTimer()
        timer.start()
        config.load()

        def logLevel = LogLevel.parse(config.loggingSection().level.toString().toUpperCase())
        logger.currentLevel = logLevel

        jobQueue = new LinkedBlockingQueue<Job>(config.ciSection()['queueSize'] as int)
        storage.storagePath = new File(configRoot, "storage").toPath()
        storage.start()
        new File(configRoot, 'logs').mkdirs()
        pluginManager.loadPlugins()

        eventBus.dispatch("ci.init", [
                time: System.currentTimeMillis()
        ])

        timer.stop()

        logger.info("Completed Initialization in ${timer.time} milliseconds")
    }

    /**
     * Loads Jobs from Database and Job Files
     */
    void loadJobs() {
        def jobRoot = new File(configRoot, "jobs")

        if (!jobRoot.exists()) {
            jobRoot.mkdir()
        }

        def jobStorage = storage.get("jobs")

        FileMatcher.create(jobRoot).withExtension("groovy") { File file ->
            def job = new Job(this, file)

            if (jobStorage.containsKey(job.name)) {
                def jobInfo = jobStorage[job.name] as Map<String, Object>
                job.status = JobStatus.parse(jobInfo.status as int)
            } else {
                jobStorage[job.name] = [
                    status: JobStatus.NOT_STARTED.ordinal()
                ]
            }

            jobs[job.name] = job
        }

        logger.info "Loaded ${jobs.size()} jobs."

        eventBus.dispatch("ci.jobs.loaded")
    }

    /**
     * Adds the Specified Job to the Queue
     * @param job Job to Add to Queue
     */
    void runJob(Job job) {
        Thread.start("Builder[${job.name}]") { ->
            def number = (job.history.latestBuild?.number ?: 0) + 1
            def lastStatus = number == 1 ? JobStatus.NOT_STARTED : job.status
            job.status = JobStatus.WAITING

            logger.debug "Job '${job.name}' has been queued"

            jobQueue.put(job)


            def checkJobInQueue = {
                jobQueue.count {
                    it.name == job.name
                } != 1
            }

            while (checkJobInQueue()) {
                //noinspection GroovySwitchStatementWithNoDefault
                switch (job.status) {
                    case JobStatus.SUCCESS || JobStatus.FAILURE: break
                }
            }

            // Update Number
            number = (job.history.latestBuild?.number ?: 0) + 1

            eventBus.dispatch("ci.job.running", [
                    jobName   : job.name,
                    lastStatus: lastStatus,
                    number    : number
            ])

            def timer = new ExecutionTimer()

            timer.start()

            def success = true
            def scmShouldRun = true
            def tasksShouldRun = true

            job.status = JobStatus.RUNNING
            logger.info "Job '${job.name}' is Running"

            if (!job.buildDir.exists()) {
                job.buildDir.mkdirs()
            }

            def jobLog = new JobLog(job.logFile)

            if (scmShouldRun) {
                def scmConfig = job.SCM

                if (!scmTypes.containsKey(scmConfig.type)) {
                    logger.error "Job '${job.name}' is attempting to use a non-existant SCM Type '${scmConfig.type}!'"
                    success = false
                    tasksShouldRun = false
                }

                def scm = scmTypes[scmConfig.type as String].newInstance()

                scm.ci = this
                scm.job = job
                scm.log = jobLog

                try {
                    scm.execute()
                } catch (CIException e) {
                    logger.info "Job '${job.name}' (SCM): ${e.message}"
                    tasksShouldRun = false
                    success = false
                }
            }

            if (tasksShouldRun) {
                def tasks = job.tasks

                for (taskConfig in tasks) {
                    def id = tasks.indexOf(taskConfig) + 1
                    logger.info "Running Task ${id} of ${job.tasks.size()} for Job '${job.name}'"

                    try {
                        def taskType = taskTypes[taskConfig.taskType as String]
                        def task = taskType.newInstance()

                        task.ci = this
                        task.job = job
                        task.log = jobLog

                        task.configure(taskConfig.configClosure)

                        try {
                            task.execute()
                        } catch (e) {
                            success = false
                            job.logFile.append(e.class.simpleName + ": " + e.message)
                            break
                        }
                    } catch (CIException e) {
                        logger.info "Job '${job.name}' (Task #${id}): ${e.message}"
                    }
                }

                def artifactsDir = new File(artifactDir, "${job.name}/${number}")
                artifactsDir.mkdirs()
                job.artifacts.files.each { location ->
                    def source = new File(job.buildDir, location)
                    def target = new File(artifactsDir, source.name)
                    if (!source.exists()) {
                        jobLog.write("Artifact '${location}' does not exist. Skipping.")
                    }
                    target.bytes = source.bytes
                }
            }

            def buildTime = timer.stop()

            logger.debug "Job '${job.name}' completed in ${buildTime} milliseconds"

            if (!success) {
                logger.info "Job '${job.name}' has Failed"
                job.status = JobStatus.FAILURE
            } else {
                logger.info "Job '${job.name}' has Completed"
                job.status = JobStatus.SUCCESS
            }

            eventBus.dispatch("ci.job.done", [
                    jobName   : job.name,
                    status    : job.status,
                    buildTime : buildTime,
                    timeString: timer.toString(),
                    number    : number
            ])

            jobLog.complete()

            def log = job.logFile.text

            def base64Log = Utils.encodeBase64(log)

            def job_history = storage.get("job_history")

            def history = ((List<Map<String, Object>>) job_history.get(job.name, []))

            history.add(number: number, status: job.status.ordinal(), log: base64Log, buildTime: buildTime, timeStamp: new Date().toString())

            jobQueue.remove(job)
            logger.debug "Job '${job.name}' removed from queue"
        }
    }

    /**
     * Updates all Jobs from the Database and parses Job Files
     */
    void updateJobs() {
        jobs.values()*.reload()
    }

    /**
     * Gets where artifacts are stored
     * @return Artifact Directory
     */
    def getArtifactDir() {
        def dir = new File(configRoot, "artifacts")
        dir.mkdir()
        return dir
    }

    static CI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CI()
        } else {
            return INSTANCE
        }
    }
}
