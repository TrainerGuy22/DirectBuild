package org.directcode.ci.utils

import org.directcode.ci.core.CI
import org.directcode.ci.logging.Logger

class ReleaseInfo {
    static final Logger logger = Logger.getLogger("Release Info")
    private static Map<String, ? extends Object> info

    static {
        try {
            def loader = CI.class.classLoader
            def releaseStream = loader.getResourceAsStream("release.json")
            info = Utils.parseJSON(releaseStream.text) as Map<String, ? extends Object>
        } catch (ignored) {
            info = [:]
        }
    }

    static String gitCommitSHA() {
        return info.get("GIT_COMMIT_SHA", "Unknown") as String
    }
}
