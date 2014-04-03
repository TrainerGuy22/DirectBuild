package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.directcode.ci.api.Source
import org.directcode.ci.api.Task
import org.directcode.ci.config.CiConfig
import org.directcode.ci.core.plugins.PluginManager
import org.directcode.ci.jobs.Job
import org.directcode.ci.jobs.JobStatus
import org.directcode.ci.logging.LogLevel
import org.directcode.ci.logging.Logger
import org.directcode.ci.source.NoneSource
import org.directcode.ci.tasks.CommandTask
import org.directcode.ci.utils.ExecutionTimer
import org.directcode.ci.utils.FileMatcher
import org.directcode.ci.utils.HTTP
import org.directcode.ci.web.WebServer
import org.jetbrains.annotations.NotNull

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
    File configRoot

    /**
     * Plugin Manager
     */
    final PluginManager pluginManager

    /**
     * CI Configuration
     */
    final CiConfig config

    /**
     * CI Storage System
     */
    final CIStorage storage

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
    final WebServer webServer

    /**
     * CI Event Bus
     */
    final EventBus eventBus

    private CI() {
        configRoot = new File(".").absoluteFile
        config = new CiConfig()
        eventBus = new EventBus()
        webServer = new WebServer()
        storage = new CIStorage()
        pluginManager = new PluginManager()
    }

    /**
     * Starts CI Server
     */
    void start() {
        init()
        loadJobs()
        Thread.startDaemon { ->
            logger.debug("Extracting WWW Resources")
            ResourceExtractor.extractWWW(new File(configRoot, "www"))
            logger.debug("Starting Web Server")
            webServer.start(config.webSection().get("port", 8080) as int, config.webSection().get("host", "0.0.0.0") as String)
        }
    }

    /**
     * Initializes this CI Server
     */
    private void init() {
        def timer = new ExecutionTimer()
        timer.start()

        debuggingSystem()

        config.configFile = new File(configRoot, "config.groovy")

        config.load()

        eventBus.dispatch("ci.config.loaded")

        loggingSystem()

        storage.storagePath = new File(configRoot, "storage").absoluteFile.toPath()
        storage.start()
        eventBus.dispatch("ci.storage.started")

        jobQueue = new JobQueue(config.ciSection().get("builders", 4) as int)

        new File(configRoot, 'logs').absoluteFile.mkdirs()

        loadBuiltins()

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

    /**
     * Loads Builtin Tasks and Sources
     */
    private void loadBuiltins() {
        registerSource("none", NoneSource)
        registerTask("command", CommandTask)
    }

    /**
     * Loads Jobs from Database and Job Files
     */
    void loadJobs() {
        def jobRoot = new File(configRoot, "jobs")

        if (!jobRoot.exists()) {
            jobRoot.mkdir()
        }

        Map<String, ? extends Object> jobStorage = storage.get("jobs")

        FileMatcher.create(jobRoot).withExtension("groovy") { File file ->
            def job = new Job(file)

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
    Build runJob(@NotNull Job job) {
        if (!job.shouldBuild()) {
            return null
        } else {
            return jobQueue.add(job)
        }
    }
    
    Build build(@NotNull Job job) {
        return runJob(job)
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

    void registerTask(@NotNull String name, @NotNull Class<? extends Task> taskType, Closure callback = {}) {
        taskTypes[name] = taskType
        eventBus.dispatch("ci.task.register", [name: name, type: taskType])
        callback()
    }

    void registerSource(@NotNull String name, @NotNull Class<? extends Source> sourceType, Closure callback = {}) {
        sourceTypes[name] = sourceType
        eventBus.dispatch("ci.source.register", [name: name, type: sourceType])
        callback()
    }

    static CI get() {
        if (INSTANCE == null) {
            INSTANCE = new CI()
        } else {
            return INSTANCE
        }
    }

    void unload() {
        eventBus.dispatch("ci.shutdown.start")
        webServer.server.close()
        if (INSTANCE == this) {
            INSTANCE = null
        }
        eventBus.dispatch("ci.shutdown.complete")
    }
    
    Job getJobByName(String name) {
        return jobs[name]
    }
    
    Class<? extends Task> getTaskByName(String taskName) {
        return taskTypes[taskName]
    }
    
    Class<? extends Source> getSourceByName(String sourceName) {
        return sourceTypes[sourceName]
    }
}
