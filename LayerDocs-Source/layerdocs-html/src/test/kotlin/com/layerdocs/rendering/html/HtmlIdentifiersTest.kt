package com.layerdocs.rendering.html

import com.layerdocs.core.ast.attributes.id.getId
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.rendering.html.node.LayerDocsHtmlNodeRenderer
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for generation of HTML ids via [HtmlIdentifierProvider].
 */
class HtmlIdentifiersTest {
    private val provider = HtmlIdentifierProvider.of(LayerDocsHtmlNodeRenderer(MutableContext(LayerDocsFlavor)))

    private fun assertIdEquals(
        expected: String,
        headingText: String,
    ) {
        assertEquals(
            expected,
            provider.getId(Heading(1, listOf(Text(headingText)))),
        )
    }

    @Test
    fun `with uppercase`() {
        assertIdEquals("abc", "Abc")
    }

    @Test
    fun `with spaces`() {
        assertIdEquals("abc-def", "Abc Def")
    }

    @Test
    fun `with tabs`() {
        assertIdEquals("abc-def", "Abc\tDef")
    }

    @Test
    fun `with special characters`() {
        assertIdEquals("hello-world", "Hello, World!")
    }

    @Test
    fun `with continuous special characters`() {
        assertIdEquals("hello-world", "Hello,,,   World!!")
    }

    @Test
    fun `with numbers`() {
        assertIdEquals("abc-123", "Abc 123")
    }

    @Test
    fun `with leading numbers`() {
        assertIdEquals("_123abc", "123abc")
    }

    @Test
    fun `with accented letters`() {
        assertIdEquals("abc-déf", "Abc Déf")
    }

    @Test
    fun `with chinese characters`() {
        assertIdEquals("abc-你好", "Abc 你好")
        assertIdEquals("你好-abc", "你好 abc")
    }
}
