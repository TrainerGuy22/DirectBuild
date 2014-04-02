package simpleci.test

import org.directcode.ci.utils.CommandFinder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static junit.framework.TestCase.assertTrue

class CommandFinderTest {

    @Before
    void before() {
        TestUtils.setupCIInstance()
    }

    @Test
    void testShellIsValid() {
        def shell = CommandFinder.shell()
        assertTrue shell.exists()
    }

    @After
    void after() {
        TestUtils.unloadCIInstance()
    }
}
