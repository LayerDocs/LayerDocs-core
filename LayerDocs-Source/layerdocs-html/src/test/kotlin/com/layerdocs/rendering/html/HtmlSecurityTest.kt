package com.layerdocs.rendering.html

import com.layerdocs.core.ast.attributes.presence.markMathPresence
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.document.deepCopy
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.rendering.html.post.HtmlPostRenderer
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for system security from HTML code injection and other vulnerabilities.
 */
class HtmlSecurityTest {
    private fun testMacro(
        name: String,
        content: String,
        expectedSnippet: String,
    ) {
        val context = MutableContext(LayerDocsFlavor)

        context.documentInfo = context.documentInfo.deepCopy(texMacros = mapOf(name to content))
        context.attributes.markMathPresence()

        val postRenderer = HtmlPostRenderer(context)
        val result = postRenderer.wrap("")

        assertTrue(expectedSnippet in result, "Expected snippet not found in output: $expectedSnippet")
    }

    @Test
    fun `injection in tex macro content`() {
        testMacro(
            name = "\\hello",
            content = "\", function() {}",
            expectedSnippet = "\"\\\\hello\": \"\\\", function() {}\"",
        )
    }

    @Test
    fun `injection in tex macro name`() {
        testMacro(
            name = """\hello": "", function() {}""",
            content = "\\text {hello}",
            expectedSnippet = "\"\\\\hello\\\": \\\"\\\", function() {}\": \"\\\\text {hello}\"",
        )
    }
}
