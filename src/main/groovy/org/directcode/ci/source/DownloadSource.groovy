package org.directcode.ci.source

import jpower.core.utils.NetUtils
import org.directcode.ci.api.Source
import org.directcode.ci.exception.TaskFailedException

class DownloadSource extends Source {

    @Override
    void execute() {
        def urls = [:]

        if (option("urls")) {
            urls.putAll(option("urls") as Map<String, String>)
        } else if (option("url") && option("to")) {
            urls[option("to")] = option("url")
        } else {
            throw new TaskFailedException("The parameters 'urls' or 'url' and 'to' are required")
        }

        for (entry in urls.entrySet()) {
            def file = new File(job.buildDir, entry.key as String)
            file.parentFile.mkdirs()
            log.write("Downloading '${entry.value}' to '${file.absolutePath}'")
            NetUtils.download(entry.value as String, file)
        }
    }
}
