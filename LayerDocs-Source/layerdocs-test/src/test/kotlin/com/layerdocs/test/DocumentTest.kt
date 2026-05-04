package com.layerdocs.test

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.attributes.presence.hasCode
import com.layerdocs.core.ast.attributes.presence.hasMath
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.context.options.HtmlOptions
import com.layerdocs.core.document.DocumentAuthor
import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.document.layout.page.PageOrientation
import com.layerdocs.core.document.layout.page.PageSizeFormat
import com.layerdocs.core.document.size.Size
import com.layerdocs.core.document.size.Sizes
import com.layerdocs.core.misc.color.NamedColor
import com.layerdocs.core.pipeline.error.BasePipelineErrorHandler
import com.layerdocs.stdlib.pageFormat
import com.layerdocs.stdlib.paragraphStyle
import com.layerdocs.test.util.execute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for document metadata and attributes.
 */
class DocumentTest {
    @Test
    fun `initial state`() {
        execute("") {
            assertEquals("", it)
            assertIs<AstRoot>(attributes.root)
            assertFalse(attributes.hasCode)
            assertFalse(attributes.hasMath)
            assertEquals(DocumentType.PLAIN, documentInfo.type)
            assertNull(documentInfo.name)
            assertEquals(0, documentInfo.authors.size)
            assertNull(documentInfo.description)
            assertTrue(documentInfo.keywords.isEmpty())
            assertNull(documentInfo.locale)
            assertTrue(documentInfo.layout.pageFormats.isEmpty())
            assertNull(documentInfo.layout.paragraphStyle.spacing)
            assertEquals(HtmlOptions(), options.html)
        }
    }

    @Test
    fun `document setup`() {
        execute(
            """
            .docname {My LayerDocs document}
            .docdescription {A comprehensive guide to LayerDocs}
            
            .dockeywords
              - documentation
              - markdown
              - typesetting
            
            .docauthors
              - SatyamPote
                - website: https://SatyamPote.eu
              - Giorgio
                - website: https://github.com/SatyamPote
              - Gio
            .doctype {slides}
            .doclang {english}
            .theme {darko} layout:{minimal}
            .pageformat size:{A3} orientation:{landscape} margin:{3cm 2px} bordercolor:{green} columns:{4} alignment:{end}
            .paragraphstyle lineheight:{2.0} spacing:{1.5} indent:{2}
            .slides transition:{zoom} speed:{fast}
            .autopagebreak maxdepth:{3}
            """.trimIndent(),
        ) {
            assertEquals("My LayerDocs document", documentInfo.name)
            assertEquals("A comprehensive guide to LayerDocs", documentInfo.description)
            assertEquals(listOf("documentation", "markdown", "typesetting"), documentInfo.keywords)
            assertEquals(
                listOf(
                    DocumentAuthor("SatyamPote", mapOf("website" to "https://SatyamPote.eu")),
                    DocumentAuthor("Giorgio", mapOf("website" to "https://github.com/SatyamPote")),
                    DocumentAuthor("Gio", mapOf()),
                ),
                documentInfo.authors,
            )
            assertEquals("en", documentInfo.locale?.tag)
            assertEquals(DocumentType.SLIDES, documentInfo.type)
            assertEquals("darko", documentInfo.theme?.color)
            assertEquals("minimal", documentInfo.theme?.layout)

            val pageFormat = documentInfo.layout.pageFormats.last()

            PageSizeFormat.A3.getBounds(PageOrientation.LANDSCAPE).let { bounds ->
                assertEquals(bounds.width, pageFormat.pageWidth)
                assertEquals(bounds.height, pageFormat.pageHeight)
            }

            assertEquals(
                Sizes(
                    vertical = Size(3.0, Size.Unit.CENTIMETERS),
                    horizontal = Size(2.0, Size.Unit.PIXELS),
                ),
                pageFormat.margin,
            )

            assertNull(pageFormat.contentBorderWidth)
            assertEquals(NamedColor.GREEN.color, pageFormat.contentBorderColor)
            assertEquals(4, pageFormat.columnCount)
            assertEquals(Container.TextAlignment.END, pageFormat.alignment)

            assertEquals(2.0, documentInfo.layout.paragraphStyle.lineHeight)
            assertEquals(1.5, documentInfo.layout.paragraphStyle.spacing)
            assertEquals(2.0, documentInfo.layout.paragraphStyle.indent)
        }
    }

    @Test
    fun `document cannot have blank name`() {
        assertFails {
            execute(".docname { }") {}
        }

        execute(".docname { }", errorHandler = BasePipelineErrorHandler()) {
            assertNull(documentInfo.name)
        }
    }

    @Test
    fun `document metadata echo`() {
        execute(
            """
            .docname {My LayerDocs document}
            
            .dockeywords
              - layerdocs
              - markdown
              - documentation
            
            .docauthors
              - SatyamPote
                - country: Italy
            .doctype {slides}
            .doclang {english}

            .docdescription
                A comprehensive guide to LayerDocs

            .docname .text {.docname} size:{tiny}.

            .docdescription

            .docauthors

            #! .docauthor

            .doctype

            .doclang
            
            .dockeywords
            """.trimIndent(),
        ) {
            assertEquals(
                "<p>My LayerDocs document " +
                    "<span class=\"size-tiny\">My LayerDocs document</span>.</p>" +
                    "<p>A comprehensive guide to LayerDocs</p>" +
                    "<table>" +
                    "<thead><tr><th>Key</th><th>Value</th></tr></thead>" +
                    "<tbody>" +
                    "<tr><td>SatyamPote</td><td>" +
                    "<table><thead><tr><th>Key</th><th>Value</th></tr></thead>" +
                    "<tbody><tr><td>country</td><td><p>Italy</p></td></tr></tbody></table></td></tr>" +
                    "</tbody>" +
                    "</table>" +
                    "<h1 data-decorative=\"\">SatyamPote</h1>" +
                    "<p>slides</p>" +
                    "<p>English</p>" +
                    "<ol><li><p>layerdocs</p></li><li><p>markdown</p></li><li><p>documentation</p></li></ol>",
                it,
            )
        }
    }

    // HTML options

    @Test
    fun `html options base url`() {
        execute(
            ".htmloptions baseurl:{https://example.com}",
        ) {
            assertEquals(HtmlOptions(baseUrl = "https://example.com"), options.html)
        }
    }

    @Test
    fun `html options default`() {
        execute("") {
            assertEquals(HtmlOptions(), options.html)
            assertNull(options.html.baseUrl)
        }
    }

    @Test
    fun `document info modification from scope`() {
        execute(
            """
            .docname {Original Name}
            
            .if {yes}
                .docname {Modified Name}
            """.trimIndent(),
        ) {
            assertEquals("Modified Name", documentInfo.name)
        }
    }
}
