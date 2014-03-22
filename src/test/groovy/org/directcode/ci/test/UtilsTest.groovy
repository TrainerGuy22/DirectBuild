package org.directcode.ci.test

import org.directcode.ci.utils.Utils
import org.junit.Test

class UtilsTest extends CITest {

    @Test
    void testFindCommand() {
        def actual = Utils.findCommandOnPath("sh")
        assertFalse "Command was not found!", actual == null
    }
}