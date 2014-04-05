package directbuild.test

import org.directcode.ci.core.CI
import org.directcode.ci.utils.Utils
import org.junit.BeforeClass

import static org.junit.Assert.assertEquals

abstract class CITest {

    static CI ci

    @BeforeClass
    static void setupCIInstance() {
        if (!ci) {
            ci = CI.get()
            ci.configRoot = new File("src/test/work/")
            ci.configRoot.deleteDir()
            ci.configRoot.mkdirs()
            def jobDir = new File(ci.configRoot.absoluteFile, "jobs")

            def result = Utils.execute {
                executable = "git"
                argument("clone")
                argument("git://github.com/DirectBuild/test-jobs.git")
                argument(jobDir.absolutePath)
            }

            assertEquals(0, result.code)
            ci.start()
        }
    }
}
