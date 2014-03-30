package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.directcode.ci.api.Source
import org.directcode.ci.api.Task
import org.directcode.ci.config.CiConfig
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobStatus
import org.directcode.ci.logging.LogLevel
import org.directcode.ci.logging.Logger
import org.directcode.ci.plugins.PluginManager
import org.directcode.ci.source.GitSource
import org.directcode.ci.source.NoneSource
import org.directcode.ci.tasks.*
import org.directcode.ci.utils.ExecutionTimer
import org.directcode.ci.utils.FileMatcher
import org.directcode.ci.utils.HTTP
import org.directcode.ci.web.VertxManager

@CompileStatic
class CI {

    private static CI INSTANCE

    /**
     * Main CI Logger
     */
    static final Logger logger = Logger.getLogger("CI")

    /**
     * Configuration Root
     */
    File configRoot = new File(".").absoluteFile

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
    final Map<String, Class<? extends Task>> taskTypes = [:]

    /**
     * Source Code Manager Types
     */
    final Map<String, Class<? extends Source>> sourceTypes = [:]

    /**
     * CI Jobs
     */
    final Map<String, Job> jobs = [:]

    /**
     * Job Queue System
     */
    JobQueue jobQueue

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

        debuggingSystem()

        eventBus.dispatch("ci.config.loaded")

        config.load()

        loggingSystem()

        storage.storagePath = new File(configRoot, "storage").absoluteFile.toPath()
        storage.start()
        eventBus.dispatch("ci.storage.started")

        jobQueue = new JobQueue(this, config.ciSection()['builders'] as int)

        new File(configRoot, 'logs').absoluteFile.mkdirs()


        loadBuiltins()

        eventBus.dispatch("ci.builtins.loaded")

        pluginManager.loadPlugins()

        eventBus.dispatch("ci.plugins.loaded")

        eventBus.dispatch("ci.init", [
                time: System.currentTimeMillis()
        ])

        timer.stop()

        logger.info("Completed Initialization in ${timer.time} milliseconds")
    }

    private void loggingSystem() {
        // Initialize a few loggers
        HTTP.logger
        CrashReporter.logger
        Build.logger
        JobQueue.logger


        def logLevel = LogLevel.parse(config.loggingSection().level.toString().toUpperCase())

        Logger.globalLogLevel = logLevel

        def logFile = new File(configRoot, "ci.log")

        if (logFile.exists()) {
            logFile.renameTo("ci.log.old")
        }

        Logger.logAllTo(logFile.toPath())
    }

    private void loadBuiltins() {
        registerSource("git", GitSource)
        registerSource("none", NoneSource)
        registerTask("gradle", GradleTask)
        registerTask("groovy", GroovyScriptTask)
        registerTask("command", CommandTask)
        registerTask("git", GitTask)
        registerTask("make", MakeTask)
        registerTask("ant", AntTask)
        registerTask("maven", MavenTask)
    }

    /**
     * Loads Jobs from Database and Job Files
     */
    void loadJobs() {
        File jobRoot = new File(configRoot, "jobs")

        if (!jobRoot.exists()) {
            jobRoot.mkdir()
        }

        Map<String, ? extends Object> jobStorage = storage.get("jobs")

        FileMatcher.create(jobRoot).withExtension("groovy") { File file ->
            def job = new Job(this, file)

            if (jobStorage.containsKey(job.name)) {
                def jobInfo = jobStorage[job.name] as Map<String, Object>
                job.status = JobStatus.parse(jobInfo.status as int)
            } else {
                def info = [
                        status: JobStatus.NOT_STARTED.ordinal()
                ]
                jobStorage[job.name] = info
            }

            jobs[job.name] = job
        }

        logger.info "Loaded ${jobs.size()} jobs."

        eventBus.dispatch("ci.jobs.loaded")
    }

    private void debuggingSystem() {
        eventBus.on("ci.task.register") { event ->
            logger.debug("Registered task '${event.name}' with type '${(event["type"] as Class<?>).name}'")
        }
        eventBus.on("ci.source.register") { event ->
            logger.debug("Registered Source '${event.name}' with type '${(event["type"] as Class<?>).name}'")
        }
    }

    /**
     * Adds the Specified Job to the Queue
     * @param job Job to Add to Queue
     * @return A Build that can be used to track status information
     */
    Build runJob(Job job) {
        return jobQueue.add(job)
    }

    /**
     * Updates all Jobs from the Database and parses Job Files
     */
    void updateJobs() {
        jobs.values()*.reload()
        eventBus.dispatch("ci.jobs.reloaded")
    }

    /**
     * Gets where artifacts are stored
     * @return Artifact Directory
     */
    File getArtifactDir() {
        File dir = new File(configRoot, "artifacts").absoluteFile
        dir.mkdir()
        return dir
    }

    void registerTask(String name, Class<? extends Task> taskType, Closure callback = {}) {
        taskTypes[name] = taskType
        eventBus.dispatch("ci.task.register", [name: name, type: taskType])
        callback()
    }

    void registerSource(String name, Class<? extends Source> sourceType, Closure callback = {}) {
        sourceTypes[name] = sourceType
        eventBus.dispatch("ci.source.register", [name: name, type: sourceType])
        callback()
    }

    static CI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CI()
        } else {
            return INSTANCE
        }
    }
}
