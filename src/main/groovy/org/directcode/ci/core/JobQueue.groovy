package org.directcode.ci.core

import groovy.transform.CompileStatic
import org.directcode.ci.jobs.Job
import org.directcode.ci.logging.Logger

@CompileStatic
class JobQueue {
    static final Logger logger = Logger.getLogger("JobQueue")
    private final Set<Build> buildQueue
    private final CI ci
    private final Set<Builder> builders
    private final Map<String, Integer> numbers

    JobQueue(CI ci, int builderCount) {
        this.ci = ci
        this.buildQueue = new HashSet<>()
        this.builders = new HashSet<>(builderCount)
        1.upto(builderCount) { id ->
            def builder = new Builder(ci, id as int)
            builder.start()
            builders.add(builder)
        }
        this.numbers = ci.storage.get("build_numbers") as Map<String, Integer>
        addShutdownHook { ->
            builders.each { builder ->
                builder.shouldRun = false
            }
        }
    }

    Set<Build> builds() {
        return buildQueue
    }

    boolean isBuilding(Job job, Builder exclude = null) {
        for (builder in builders) {
            if (!builder.busy || (exclude != null && builder.is(exclude))) {
                continue
            }
            if (builder.current().job.name == job.name) {
                return true
            }
        }
        return false
    }

    synchronized Build add(Job job) {
        def number = numbers.get(job.name, 0) + 1
        numbers[job.name] = number
        def build = new Build(job, number)
        def available = builders.findAll { builder ->
            !builder.busy
        }
        if (!available) {
            builders.first().queue().add(build)
        } else {
            available.first().queue().add(build)
        }
        return build
    }

    Set<Builder> getAllBuilding(Job job) {
        def out = []
        for (builder in builders) {
            if (builder.busy && builder.current().job == job) {
                out.add(job)
            }
        }
        return out.toSet()
    }
}
