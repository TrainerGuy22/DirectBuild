package org.directcode.ci.jobs

enum JobStatus {
    SUCCESS, FAILURE, NOT_STARTED, RUNNING, WAITING;

    @Override
    String toString() {
        return this.name().toLowerCase().capitalize().replace('_', ' ')
    }

    static JobStatus parse(int id) {
        if (id < 0 || id >= values().size()) {
            return NOT_STARTED
        }
        return values()[id]
    }
}