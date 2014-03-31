package org.directcode.ci.jobs

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

@CompileStatic
class JobLog {
    final File file
    final PrintWriter out

    JobLog(@NotNull File file) {
        this.file = file
        this.out = file.newPrintWriter()
    }

    void write(@NotNull String line) {
        out.println(line)
        out.flush()
    }

    void complete() {
        out.flush()
        out.close()
    }
}
