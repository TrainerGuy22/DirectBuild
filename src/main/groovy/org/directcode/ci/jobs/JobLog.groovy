package org.directcode.ci.jobs

class JobLog {
    final File file
    final PrintWriter out

    JobLog(File file) {
        this.file = file
        this.out = file.newPrintWriter()
    }

    void write(String line) {
        out.println(line)
        out.flush()
    }

    void complete() {
        out.flush()
        out.close()
    }
}
