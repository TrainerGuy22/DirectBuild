package org.directcode.ci.utils

class ExecuteResult {
    final List<String> output
    final int code

    ExecuteResult(int code, List<String> output) {
        this.code = code
        this.output = output
    }
}
