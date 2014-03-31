package org.directcode.ci.core

import groovy.transform.CompileStatic

@CompileStatic
class Builder implements Runnable {
    private final CI ci
    private final int id
    private final Thread thread = new Thread(this)

    private boolean busy = false

    private final Set<Build> builderQueue = new HashSet<>()

    protected boolean shouldRun = true

    private Build current = null

    Builder(CI ci, int id) {
        this.ci = ci
        this.id = id
    }

    void start() {
        thread.start()
    }

    @Override
    void run() {
        ci.logger.debug("Builder ${id} starting up.")
        while (shouldRun) {
            while (builderQueue.empty) {
                sleep(2)
            }
            busy = true
            ci.logger.debug("Builder ${id} is busy.")
            def build = builderQueue.first()
            current = build
            builderQueue.remove(build)

            ci.eventBus.dispatch("ci.build.queued", [
                    jobName: build.job.name,
                    number : build.number,
                    build  : build
            ])

            def count = 0
            while (ci.jobQueue.isBuilding(build.job, this)) {
                count++
                if (count == 100) {
                    count = 0
                    build.logger.debug("Build '${build.job.name}:${build.number}' waiting for another build to complete.")
                }
                sleep(50)
            }
            build.execute()
            busy = false
            ci.logger.debug("Builder ${id} is no longer busy.")
        }
        ci.logger.debug("Builder ${id} shutting down.")
    }

    boolean isBusy() {
        return busy
    }

    Set<Build> queue() {
        return builderQueue
    }

    Build current() {
        return current
    }

    int id() {
        return id
    }
}
