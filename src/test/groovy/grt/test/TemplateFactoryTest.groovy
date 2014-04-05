package grt.test

import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals

class TemplateFactoryTest {
    @Test
    @Ignore("Not Yet Completed")
    void testFactoryDefineComponentWorks() {
        def factory = GrtTestUtil.factory()
        factory.define("test") { opts ->
            build {
                p("Test")
            }
        }
        def template = factory.create(new StringReader("""\
        <% component("test") %>
        """.stripIndent()))
        assertEquals("<p>Test</p>", template.make([:]).toString())
    }
}
