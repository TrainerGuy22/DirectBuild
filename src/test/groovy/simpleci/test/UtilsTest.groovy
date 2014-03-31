package simpleci.test

import org.directcode.ci.utils.Utils
import org.intellij.lang.annotations.Language
import org.junit.Test

class UtilsTest {
    @Test
    void testJsonPrettyPrintIsValidJson() {
        @Language("JSON")
        def input = """
        { "object": { "key": "value" } }
        """.stripIndent()
        def pretty = Utils.prettyJSON(input)
        Utils.parseJSON(pretty)
    }
}
