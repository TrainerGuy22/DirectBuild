package simpleci.test

import org.directcode.ci.core.CI

class TestUtils {
    static void setupCIInstance() {
        def ci = CI.get()
        ci.configRoot = new File("src/test/work/")
        if (!ci.configRoot.exists()) {
            ci.configRoot.mkdirs()
        }
        ci.start()
    }

    static void unloadCIInstance() {
        CI.get().unload()
    }
}
