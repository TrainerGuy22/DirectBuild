package simpleci.test

import org.directcode.ci.utils.CommandFinder
import org.junit.Test

import static junit.framework.TestCase.assertTrue

class CommandFinderTest extends CITest {

    @Test
    void testShellIsValid() {
        assertTrue CommandFinder.shell().exists()
    }
}
