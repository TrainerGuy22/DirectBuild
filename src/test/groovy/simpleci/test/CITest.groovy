package simpleci.test

import org.directcode.ci.core.CI
import org.junit.After
import org.junit.Before

class CITest {

    protected CI ci

    @Before
    void setupCIInstance() {
        ci = CI.get()
        ci.configRoot = new File("src/test/work/")
        if (!ci.configRoot.exists()) {
            ci.configRoot.mkdirs()
        }
        ci.start()
    }

    @After
    void unloadCIInstance() {
        ci.unload()
    }
}
