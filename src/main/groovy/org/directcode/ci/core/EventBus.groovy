package org.directcode.ci.core

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import jpower.core.Task as PowerTask
import jpower.core.WorkerPool
import org.directcode.ci.utils.MultiMap
import org.jetbrains.annotations.NotNull

/**
 * A Groovy Event Bus that uses JPower's Worker Pool
 */
@CompileStatic
class EventBus {
    private final MultiMap<Closure<?>> handlers = new MultiMap<Closure<?>>()
    private final WorkerPool workerPool = new WorkerPool(4)

    /**
     * Hook into an Event
     * @param name Event Name
     * @param handler Event Handler
     */
    void on(@NotNull String name,
            @NotNull @ClosureParams(value = SimpleType.class, options = "java.util.Map") Closure handler) {
        handlers.add(name, handler)
    }

    /**
     * Dispatch an Event
     * @param data Event Data
     */
    void dispatch(@NotNull String eventName, @NotNull Map<String, ? extends Object> options = [:]) {
        if (handlers.empty(eventName)) { // No Event Handlers to call
            return
        }
        def eventHandlers = handlers[eventName]
        workerPool.submit(new PowerTask() {
            @Override
            void execute() {
                for (Closure handler in eventHandlers) {
                    handler(options)
                }
            }
        })
    }

    /**
     * Get all Handlers
     * @return Handlers
     */
    MultiMap<Closure<?>> handlers() {
        return handlers
    }

    /**
     * Gets all Handlers for Event
     * @param name Event Name
     * @return Handlers
     */
    List<Closure<?>> handlers(@NotNull String name) {
        return handlers()[name]
    }
}