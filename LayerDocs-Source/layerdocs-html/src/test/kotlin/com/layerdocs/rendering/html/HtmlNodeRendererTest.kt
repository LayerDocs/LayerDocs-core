@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.layerdocs.rendering.html

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.reference.setDefinition
import com.layerdocs.core.ast.base.block.BlockQuote
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.base.block.FootnoteDefinition
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.HorizontalRule
import com.layerdocs.core.ast.base.block.Html
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.OrderedList
import com.layerdocs.core.ast.base.block.list.TaskListItemVariant
import com.layerdocs.core.ast.base.block.list.UnorderedList
import com.layerdocs.core.ast.base.block.setIndex
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.Comment
import com.layerdocs.core.ast.base.inline.CriticalContent
import com.layerdocs.core.ast.base.inline.Emphasis
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.base.inline.LineBreak
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.ReferenceFootnote
import com.layerdocs.core.ast.base.inline.ReferenceImage
import com.layerdocs.core.ast.base.inline.ReferenceLink
import com.layerdocs.core.ast.base.inline.Strikethrough
import com.layerdocs.core.ast.base.inline.Strong
import com.layerdocs.core.ast.base.inline.StrongEmphasis
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.ast.dsl.buildBlock
import com.layerdocs.core.ast.dsl.buildBlocks
import com.layerdocs.core.ast.dsl.buildInline
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.ast.layerdocs.block.Box
import com.layerdocs.core.ast.layerdocs.block.Clipped
import com.layerdocs.core.ast.layerdocs.block.Collapse
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.ast.layerdocs.block.FileTree
import com.layerdocs.core.ast.layerdocs.block.FileTreeEntry
import com.layerdocs.core.ast.layerdocs.block.ImageFigure
import com.layerdocs.core.ast.layerdocs.block.Math
import com.layerdocs.core.ast.layerdocs.block.NavigationContainer
import com.layerdocs.core.ast.layerdocs.block.PageBreak
import com.layerdocs.core.ast.layerdocs.block.list.FocusListItemVariant
import com.layerdocs.core.ast.layerdocs.inline.IconImage
import com.layerdocs.core.ast.layerdocs.inline.InlineCollapse
import com.layerdocs.core.ast.layerdocs.inline.Keybinding
import com.layerdocs.core.ast.layerdocs.inline.LastHeading
import com.layerdocs.core.ast.layerdocs.inline.MathSpan
import com.layerdocs.core.ast.layerdocs.inline.TextSymbol
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.TextTransformData
import com.layerdocs.core.attachMockPipeline
import com.layerdocs.core.bibliography.Bibliography
import com.layerdocs.core.bibliography.BibliographyEntry
import com.layerdocs.core.bibliography.style.BibliographyEntryLabelProviderStrategy
import com.layerdocs.core.bibliography.style.BibliographyStyle
import com.layerdocs.core.context.Context
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.options.MutableContextOptions
import com.layerdocs.core.document.size.Sizes
import com.layerdocs.core.document.size.cm
import com.layerdocs.core.document.size.inch
import com.layerdocs.core.document.size.percent
import com.layerdocs.core.document.size.px
import com.layerdocs.core.flavor.base.BaseMarkdownFlavor
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.function.value.data.Range
import com.layerdocs.core.misc.color.Color
import com.layerdocs.core.misc.color.decoder.HexColorDecoder
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.core.pipeline.Pipelines
import com.layerdocs.core.readSource
import com.layerdocs.core.rendering.NodeRenderer
import com.layerdocs.core.util.node.toPlainText
import com.layerdocs.core.util.normalizeLineSeparators
import com.layerdocs.rendering.html.extension.html
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * HTML node rendering tests.
 */
class HtmlNodeRendererTest {
    private fun readParts(path: String) =
        readSource("/rendering/$path")
            .normalizeLineSeparators()
            .split("\n---\n")
            .map { it.trim() }
            .iterator()

    private fun renderer(context: Context = MutableContext(LayerDocsFlavor)): NodeRenderer {
        if (context.attachedPipeline == null) {
            // Attach a mock pipeline to the context, allowing to render pretty output
            // (since its value is retrieved from the attached pipeline)
            Pipelines.attach(
                context,
                MutableContext(context.flavor).attachMockPipeline(PipelineOptions(prettyOutput = true)),
            )
        }

        return context.flavor.rendererFactory
            .html(context)
            .nodeRenderer
    }

    private fun Node.render(context: Context = MutableContext(LayerDocsFlavor)) = this.accept(renderer(context))

    // Inline
    @Test
    fun comment() {
        assertEquals("", Comment.render())
    }

    @Test
    fun lineBreak() {
        assertEquals("<br />", LineBreak.render())
    }

    @Test
    fun criticalContent() {
        assertEquals("&amp;", CriticalContent("&").render())
        assertEquals("&gt;", CriticalContent(">").render())
        assertEquals("~", CriticalContent("~").render())
    }

    @Test
    fun link() {
        val out = readParts("inline/link.html")

        assertEquals(
            out.next(),
            Link(label = listOf(Text("Foo bar")), url = "https://google.com", title = null).render(),
        )
        assertEquals(
            out.next(),
            Link(label = listOf(Strong(listOf(Text("Foo bar")))), url = "/url", title = null).render(),
        )
        assertEquals(
            out.next(),
            Link(label = listOf(Text("Foo bar baz")), url = "url", title = listOf(Text("Title"))).render(),
        )
    }

    @Test
    fun referenceLink() {
        val out = readParts("inline/reflink.html")

        val label = listOf(Strong(listOf(Text("Foo"))))

        val context = MutableContext()

        val fallback = { Emphasis(listOf(Text("fallback"))) }

        // Resolved: label matches the reference, definition is set.
        val resolved =
            ReferenceLink(label, label, fallback).also {
                it.setDefinition(context, Link(it.label, "/url", listOf(Text("Title"))))
            }
        assertEquals(out.next(), resolved.render(context))

        // Resolved: different display label, same reference label.
        val resolvedDifferentLabel =
            ReferenceLink(listOf(Text("label")), label, fallback).also {
                it.setDefinition(context, Link(it.label, "/url", listOf(Text("Title"))))
            }
        assertEquals(out.next(), resolvedDifferentLabel.render(context))

        // Unresolved: fallback is rendered.
        assertEquals(
            out.next(),
            ReferenceLink(listOf(Text("label")), label, fallback).render(),
        )
    }

    @Test
    fun image() {
        val out = readParts("inline/image.html")

        assertEquals(
            out.next(),
            Image(
                Link(label = listOf(), url = "/url", title = null),
                width = null,
                height = null,
            ).render(),
        )
        assertEquals(
            out.next(),
            Image(
                Link(label = listOf(), url = "/url", title = listOf(Text("Title"))),
                width = null,
                height = null,
            ).render(),
        )
        assertEquals(
            out.next(),
            Image(
                Link(label = buildInline { text("Foo bar") }, url = "/url", title = null),
                width = 150.px,
                height = 100.px,
            ).render(),
        )
        assertEquals(
            out.next(),
            Image(
                Link(label = buildInline { text("Foo bar") }, url = "/url", title = listOf(Text("Title"))),
                width = 3.2.cm,
                height = null,
            ).render(),
        )
    }

    @Test
    fun referenceImage() {
        val out = readParts("inline/refimage.html")

        val label = listOf(Text("Foo"))

        val context = MutableContext()

        val fallback = { Emphasis(listOf(Text("fallback"))) }

        fun resolvedRefLink(displayLabel: InlineContent) =
            ReferenceLink(displayLabel, label, fallback).also {
                it.setDefinition(context, Link(displayLabel, "/url", listOf(Text("Title"))))
            }

        assertEquals(
            out.next(),
            ReferenceImage(
                resolvedRefLink(label),
                width = null,
                height = null,
            ).render(context),
        )
        assertEquals(
            out.next(),
            ReferenceImage(
                resolvedRefLink(listOf(Text("label"))),
                width = null,
                height = null,
            ).render(context),
        )
        assertEquals(
            out.next(),
            ReferenceImage(
                resolvedRefLink(listOf(Text("label"))),
                width = 150.px,
                height = 100.px,
            ).render(context),
        )
        assertEquals(
            out.next(),
            ReferenceImage(
                ReferenceLink(
                    listOf(Text("label")),
                    label,
                    fallback,
                ),
                width = null,
                height = null,
            ).render(),
        )
    }

    @Test
    fun figure() {
        val out = readParts("layerdocs/figure.html")

        assertEquals(
            out.next(),
            ImageFigure(
                Image(
                    Link(label = listOf(), url = "/url", title = listOf(Text(""))),
                    width = null,
                    height = null,
                ),
            ).render(),
        )
        assertEquals(
            out.next(),
            ImageFigure(
                Image(
                    Link(label = listOf(), url = "/url", title = listOf(Text("Title"))),
                    width = null,
                    height = null,
                ),
            ).render(),
        )
        assertEquals(
            out.next(),
            ImageFigure(
                Image(
                    Link(label = listOf(), url = "/url", title = listOf(Text("Title"))),
                    width = 150.px,
                    height = 100.px,
                ),
            ).render(),
        )
    }

    @Test
    fun footnoteDefinition() {
        val out = readParts("block/footnote.html")
        val context = MutableContext(LayerDocsFlavor)

        val definition =
            FootnoteDefinition(
                label = "label",
                text = buildInline { text("Foo bar") },
            )
        definition.setIndex(context, 0)

        assertEquals(
            out.next(),
            definition.render(context),
        )
    }

    @Test
    fun footnoteReference() {
        val out = readParts("inline/reffootnote.html")
        val context = MutableContext(LayerDocsFlavor)

        val definition =
            FootnoteDefinition(
                label = "label",
                text = buildInline { text("Foo bar") },
            )
        definition.setIndex(context, 0)

        val reference =
            ReferenceFootnote(
                label = "label",
                fallback = { Text("fallback") },
            )

        reference.setDefinition(context, definition)

        assertEquals(
            out.next(),
            reference.render(context),
        )
        assertEquals(
            out.next(),
            reference.render(),
        )
    }

    @Test
    fun text() {
        assertEquals("Foo bar", Text("Foo bar").render())
        assertEquals("&copy;", TextSymbol('©').render())
    }

    @Test
    fun codeSpan() {
        val out = readParts("inline/codespan.html")

        // The LayerDocs rendering wraps the content in a span which allows additional content, such as color.
        val base = MutableContext(BaseMarkdownFlavor)
        val layerdocs = MutableContext(LayerDocsFlavor)

        val spanWithColor =
            CodeSpan(
                "#FFFF00",
                CodeSpan.ColorContent(HexColorDecoder.decode("#FFFF00")!!),
            )

        assertEquals(out.next(), CodeSpan("Foo bar").render(base))
        assertEquals(out.next(), CodeSpan("<a href=\"#\">").render(base))
        assertEquals(out.next(), spanWithColor.render(layerdocs))
        assertEquals(out.next(), spanWithColor.render(base))
        assertEquals(out.next(), CodeSpan("Foo bar").render(layerdocs))
    }

    @Test
    fun emphasis() {
        val out = readParts("inline/emphasis.html")

        assertEquals(out.next(), Emphasis(listOf(Text("Foo bar"))).render())
        assertEquals(out.next(), Emphasis(listOf(Emphasis(listOf(Text("Foo bar"))))).render())
    }

    @Test
    fun strong() {
        val out = readParts("inline/strong.html")

        assertEquals(out.next(), Strong(listOf(Text("Foo bar"))).render())
        assertEquals(out.next(), Strong(listOf(Strong(listOf(Text("Foo bar"))))).render())
    }

    @Test
    fun strongEmphasis() {
        val out = readParts("inline/strongemphasis.html")

        assertEquals(out.next(), StrongEmphasis(listOf(Text("Foo bar"))).render())
        assertEquals(out.next(), StrongEmphasis(listOf(StrongEmphasis(listOf(Text("Foo bar"))))).render())
    }

    @Test
    fun strikethrough() {
        val out = readParts("inline/strikethrough.html")

        assertEquals(out.next(), Strikethrough(listOf(Text("Foo bar"))).render())
        assertEquals(out.next(), Strikethrough(listOf(Strong(listOf(Text("Foo bar"))))).render())
    }

    @Test
    fun plainTextConversion() {
        val inline: InlineContent =
            listOf(
                Text("abc"),
                Strong(
                    listOf(
                        Emphasis(
                            listOf(
                                Text("def"),
                                CodeSpan("ghi"),
                            ),
                        ),
                        CodeSpan("jkl"),
                    ),
                ),
                Text("mno"),
                CriticalContent("&"),
            )

        assertEquals("abcdefghijklmno&", inline.toPlainText())
        // Critical content is rendered differently
        assertEquals("abcdefghijklmno&amp;", inline.toPlainText(renderer()))
    }

    // Block

    @Test
    fun code() {
        val out = readParts("block/code.html")

        assertEquals(out.next(), Code("Code", language = null, showLineNumbers = true).render())
        assertEquals(out.next(), Code("Code", language = null, highlight = false).render())
        assertEquals(out.next(), Code("Code", language = null, showLineNumbers = false).render())
        assertEquals(out.next(), Code("class Point {\n    ...\n}", language = null, showLineNumbers = true).render())
        assertEquals(out.next(), Code("class Point {\n    ...\n}", language = "java", showLineNumbers = false).render())
        assertEquals(out.next(), Code("<a href=\"#\">", language = "html", showLineNumbers = true).render())
        assertEquals(
            out.next(),
            Code("class Point {\n    ...\n}", language = "java", focusedLines = Range(1, 2)).render(),
        )
        assertEquals(
            out.next(),
            Code("class Point {\n    ...\n}", language = "java", focusedLines = Range(2, null)).render(),
        )
        assertEquals(
            out.next(),
            Code("class Point {\n    ...\n}", language = "java", focusedLines = Range(null, 1)).render(),
        )
        assertEquals(
            out.next(),
            Code("class Point {\n    ...\n}", language = "java", caption = listOf(Text("A Java code example."))).render(),
        )
    }

    @Test
    fun horizontalRule() {
        assertEquals("<hr />", HorizontalRule.render())
    }

    @Test
    fun pageBreak() {
        assertEquals("<div class=\"page-break\" data-hidden=\"\">\n</div>", PageBreak().render())
    }

    @Test
    fun heading() {
        val out = readParts("block/heading.html")

        // No automatic ID, no automatic page break.
        val noIdNoPageBreak =
            MutableContext(
                LayerDocsFlavor,
                options = MutableContextOptions(autoPageBreakHeadingMaxDepth = 0, enableAutomaticIdentifiers = false),
            )

        assertEquals(out.next(), Heading(1, listOf(Text("Foo bar"))).render(noIdNoPageBreak))
        assertEquals(out.next(), Heading(2, listOf(Text("Foo bar"))).render(noIdNoPageBreak))
        assertEquals(
            out.next(),
            Heading(
                2,
                listOf(Text("Foo bar")),
                canBreakPage = false,
                canTrackLocation = false,
                excludeFromTableOfContents = true,
            ).render(noIdNoPageBreak),
        )
        assertEquals(out.next(), Heading(3, listOf(Text("Foo bar")), customId = "my-id").render(noIdNoPageBreak))
        assertEquals(out.next(), Heading(3, listOf(Strong(listOf(Text("Foo bar"))))).render(noIdNoPageBreak))
        assertEquals(out.next(), Heading(4, listOf(Text("Foo"), Emphasis(listOf(Text("bar"))))).render(noIdNoPageBreak))

        // Automatic ID, no automatic page break.
        val idNoPageBreak =
            MutableContext(
                LayerDocsFlavor,
                options = MutableContextOptions(autoPageBreakHeadingMaxDepth = 0),
            )

        assertEquals(out.next(), Heading(1, listOf(Text("Foo bar"))).render(idNoPageBreak))
        assertEquals(out.next(), Heading(1, listOf(Text("Foo bar")), customId = "custom-id").render(idNoPageBreak))
        assertEquals(out.next(), Heading(4, listOf(Text("Foo"), Emphasis(listOf(Text("bar"))))).render(idNoPageBreak))

        // Automatic ID, force page break on depth <= 2
        val autoPageBreak =
            MutableContext(LayerDocsFlavor, options = MutableContextOptions(autoPageBreakHeadingMaxDepth = 2))

        assertEquals(out.next(), Heading(1, listOf(Text("Foo bar"))).render(autoPageBreak))
        assertEquals(out.next(), Heading(2, listOf(Text("Foo bar"))).render(autoPageBreak))
        assertEquals(out.next(), Heading(3, listOf(Text("Foo bar"))).render(autoPageBreak))
        assertEquals(out.next(), Heading(4, listOf(Text("Foo bar"))).render(autoPageBreak))
    }

    private fun listItems() =
        listOf(
            ListItem(
                children =
                    listOf(
                        Paragraph(listOf(Text("A1"))),
                        HorizontalRule,
                        Paragraph(listOf(Text("A2"))),
                    ),
            ),
            ListItem(
                children =
                    listOf(
                        Paragraph(listOf(Text("B1"))),
                        HorizontalRule,
                        Paragraph(listOf(Text("B2"))),
                    ),
            ),
            ListItem(
                children =
                    listOf(
                        Paragraph(listOf(Text("C1"))),
                        HorizontalRule,
                        Paragraph(listOf(Text("C2"))),
                    ),
            ),
            ListItem(
                variants = listOf(FocusListItemVariant(isFocused = true)),
                children =
                    listOf(
                        Paragraph(listOf(Text("D1"))),
                        HorizontalRule,
                        Paragraph(listOf(Text("D2"))),
                    ),
            ),
            ListItem(
                variants = listOf(TaskListItemVariant(isChecked = true)),
                listOf(
                    Paragraph(listOf(Text("E1"))),
                    HorizontalRule,
                    Paragraph(listOf(Text("E2"))),
                ),
            ),
        )

    @Test
    fun orderedList() {
        val out = readParts("block/orderedlist.html")

        assertEquals(out.next(), OrderedList(startIndex = 1, isLoose = false, emptyList()).render())

        assertEquals(
            out.next(),
            OrderedList(
                startIndex = 1,
                isLoose = true,
                listItems(),
            ).render(),
        )

        assertEquals(
            out.next(),
            OrderedList(
                startIndex = 12,
                isLoose = true,
                listItems(),
            ).render(),
        )

        assertEquals(
            out.next(),
            OrderedList(
                startIndex = 1,
                isLoose = false,
                listItems(),
            ).also { list ->
                list.children
                    .asSequence()
                    .filterIsInstance<ListItem>()
                    .forEach { it.owner = list }
            }.render(),
        )
    }

    @Test
    fun unorderedList() {
        val out = readParts("block/unorderedlist.html")

        assertEquals(out.next(), UnorderedList(isLoose = false, emptyList()).render())

        assertEquals(
            out.next(),
            UnorderedList(
                isLoose = true,
                listItems(),
            ).render(),
        )

        assertEquals(
            out.next(),
            UnorderedList(
                isLoose = false,
                listItems(),
            ).also { list ->
                list.children
                    .asSequence()
                    .filterIsInstance<ListItem>()
                    .forEach { it.owner = list }
            }.render(),
        )
    }

    @Test
    fun html() {
        assertEquals("<p><strong>test</p></strong>", Html("<p><strong>test</p></strong>").render())
    }

    @Test
    fun paragraph() {
        val out = readParts("block/paragraph.html")

        assertEquals(out.next(), Paragraph(listOf(Text("Foo bar"))).render())
        assertEquals(out.next(), Paragraph(listOf(Text("Foo"), LineBreak, Text("bar"))).render())
    }

    @Test
    fun blockquote() {
        val out = readParts("block/blockquote.html")

        assertEquals(
            out.next(),
            buildBlock {
                blockQuote {
                    paragraph { text("Foo bar") }
                    paragraph { text("Baz bim") }
                }
            }.render(),
        )

        assertEquals(
            out.next(),
            buildBlock {
                blockQuote(attribution = { text("William Shakespeare") }) {
                    paragraph { text("To be, or not to be.") }
                    paragraph { text("That is the question.") }
                }
            }.render(),
        )

        // The 'Tip' label is not rendered here because
        // it requires the stdlib localization table.
        assertEquals(
            out.next(),
            buildBlock {
                blockQuote(
                    type = BlockQuote.Type.TIP,
                    attribution = { text("Someone") },
                ) {
                    paragraph { text("Hi there!") }
                }
            }.render(),
        )
    }

    @Test
    fun table() {
        val out = readParts("block/table.html")

        assertEquals(
            out.next(),
            Table(
                listOf(
                    Table.Column(
                        Table.Alignment.NONE,
                        header = Table.Cell(listOf(Text("A"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("C"))),
                                Table.Cell(listOf(Text("E"))),
                            ),
                    ),
                    Table.Column(
                        Table.Alignment.NONE,
                        header = Table.Cell(listOf(Text("B"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("D"))),
                                Table.Cell(listOf(Text("F"))),
                            ),
                    ),
                ),
            ).render(),
        )

        assertEquals(
            out.next(),
            Table(
                listOf(
                    Table.Column(
                        Table.Alignment.CENTER,
                        header = Table.Cell(listOf(Text("A"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("C"))),
                                Table.Cell(listOf(Text("E"))),
                            ),
                    ),
                    Table.Column(
                        Table.Alignment.RIGHT,
                        header = Table.Cell(listOf(Text("B"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("D"))),
                                Table.Cell(listOf(Strong(listOf(Text("F"))))),
                            ),
                    ),
                ),
            ).render(),
        )

        assertEquals(
            out.next(),
            Table(
                listOf(
                    Table.Column(
                        Table.Alignment.NONE,
                        header = Table.Cell(listOf(Text("A"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("C"))),
                                Table.Cell(listOf(Text("E"))),
                            ),
                    ),
                    Table.Column(
                        Table.Alignment.NONE,
                        header = Table.Cell(listOf(Text("B"))),
                        cells =
                            listOf(
                                Table.Cell(listOf(Text("D"))),
                                Table.Cell(listOf(Text("F"))),
                            ),
                    ),
                ),
                caption = listOf(Text("Table 'caption'.")),
            ).render(),
        )
    }

    // LayerDocs

    @Test
    fun mathBlock() {
        val out = readParts("block/math.html")

        assertEquals(out.next(), Math("some expression").render())
        assertEquals(out.next(), Math("\\lim_{x\\to\\infty}x").render())
    }

    @Test
    fun mathSpan() {
        val out = readParts("inline/math.html")

        assertEquals(out.next(), MathSpan("some expression").render())
        assertEquals(out.next(), MathSpan("\\lim_{x\\to\\infty}x").render())
    }

    @Test
    fun container() {
        val out = readParts("layerdocs/container.html")
        val children =
            buildBlocks {
                paragraph { text("Foo bar") }
                blockQuote { paragraph { text("Baz") } }
            }

        assertEquals(out.next(), Container(children = children).render())

        assertEquals(
            out.next(),
            Container(
                foregroundColor = Color(100, 20, 80),
                backgroundColor = Color(10, 20, 30),
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            Container(
                backgroundColor = Color(10, 20, 30),
                padding = Sizes(vertical = 2.0.cm, horizontal = 3.0.cm),
                cornerRadius = Sizes(all = 12.0.px),
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            Container(
                fullWidth = true,
                borderColor = Color(30, 20, 10),
                borderWidth = Sizes(all = 1.0.cm),
                margin = Sizes(all = 2.0.cm),
                padding = Sizes(2.0.inch, 3.percent, 4.0.inch, 5.0.inch),
                cornerRadius = Sizes(all = 6.0.px),
                alignment = Container.Alignment.CENTER,
                textAlignment = Container.TextAlignment.JUSTIFY,
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            Container(
                borderColor = Color(30, 20, 10),
                borderStyle = Container.BorderStyle.DOTTED,
                alignment = Container.Alignment.END,
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            Container(
                float = Container.FloatAlignment.END,
                className = "custom-class",
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            Container(
                textTransform =
                    TextTransformData(
                        size = TextTransformData.Size.LARGE,
                        style = TextTransformData.Style.ITALIC,
                        decoration = TextTransformData.Decoration.STRIKETHROUGH,
                        weight = TextTransformData.Weight.BOLD,
                        case = TextTransformData.Case.UPPERCASE,
                        variant = TextTransformData.Variant.SMALL_CAPS,
                    ),
                children = children,
            ).render(),
        )
    }

    @Test
    fun navigationContainer() {
        val out = readParts("layerdocs/navigationcontainer.html")
        val children = buildBlocks { paragraph { text("Nav") } }

        assertEquals(
            out.next(),
            NavigationContainer(
                role = null,
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            NavigationContainer(
                role = NavigationContainer.Role.PAGE_LIST,
                children = children,
            ).render(),
        )

        assertEquals(
            out.next(),
            NavigationContainer(
                role = NavigationContainer.Role.TABLE_OF_CONTENTS,
                children = children,
            ).render(),
        )
    }

    @Test
    fun fullSpan() {
        val out = readParts("layerdocs/fullspan.html")
        val paragraph = Paragraph(listOf(Text("Foo"), LineBreak, Text("bar")))

        assertEquals(out.next(), Container(fullColumnSpan = true, children = listOf(paragraph)).render())
    }

    @Test
    fun clipped() {
        val out = readParts("layerdocs/clipped.html")
        val paragraph = Paragraph(listOf(Text("Foo"), LineBreak, Text("bar")))

        assertEquals(out.next(), Clipped(Clipped.Clip.CIRCLE, listOf(paragraph)).render())
        assertEquals(out.next(), Clipped(Clipped.Clip.CIRCLE, listOf(paragraph, paragraph)).render())
    }

    @Test
    fun box() {
        val out = readParts("layerdocs/box.html")
        val paragraph = Paragraph(listOf(Text("Foo"), LineBreak, Text("bar")))

        assertEquals(
            out.next(),
            Box(
                title = listOf(Text("Title")),
                type = Box.Type.CALLOUT,
                padding = null,
                backgroundColor = null,
                foregroundColor = null,
                listOf(paragraph),
            ).render(),
        )

        assertEquals(
            out.next(),
            Box(
                title = listOf(Text("Title"), Emphasis(listOf(Text("Title")))),
                type = Box.Type.WARNING,
                padding = null,
                backgroundColor = null,
                foregroundColor = null,
                listOf(paragraph),
            ).render(),
        )

        assertEquals(
            out.next(),
            Box(
                title = null,
                type = Box.Type.ERROR,
                padding = 4.0.cm,
                backgroundColor = null,
                foregroundColor = null,
                listOf(paragraph),
            ).render(),
        )

        assertEquals(
            out.next(),
            Box(
                title = listOf(Text("Title")),
                type = Box.Type.ERROR,
                padding = 3.0.inch,
                backgroundColor = Color(255, 0, 120),
                foregroundColor = Color(0, 10, 25),
                listOf(paragraph),
            ).render(),
        )
    }

    @Test
    fun collapse() {
        val out = readParts("layerdocs/collapse.html")

        assertEquals(
            out.next(),
            Collapse(
                title = listOf(Emphasis(listOf(Text("Hello")))),
                isOpen = false,
                content = listOf(Strong(listOf(Text("world")))),
            ).render(),
        )

        assertEquals(
            out.next(),
            Collapse(
                title = listOf(Text("Hello")),
                isOpen = true,
                content = listOf(BlockQuote(content = listOf(Paragraph(listOf(Text("world")))))),
            ).render(),
        )
    }

    @Test
    fun `inline collapse`() {
        val out = readParts("layerdocs/inlinecollapse.html")

        assertEquals(
            out.next(),
            InlineCollapse(
                text = buildInline { text("Foo bar") },
                placeholder = buildInline { text("Placeholder") },
                isOpen = false,
            ).render(),
        )

        assertEquals(
            out.next(),
            InlineCollapse(
                text = buildInline { text("Foo bar") },
                placeholder = buildInline { text("Placeholder") },
                isOpen = true,
            ).render(),
        )
    }

    @Test
    fun keybinding() {
        val out = readParts("layerdocs/keybinding.html")

        // Primary modifier + Shift + Key
        assertEquals(
            out.next(),
            Keybinding(
                listOf(Keybinding.PrimaryModifier, Keybinding.ShiftModifier, Keybinding.Key("K")),
            ).render(),
        )

        // Alt + Key
        assertEquals(
            out.next(),
            Keybinding(
                listOf(Keybinding.AltModifier, Keybinding.Key("F4")),
            ).render(),
        )

        // Ctrl (explicit) + Key
        assertEquals(
            out.next(),
            Keybinding(
                listOf(Keybinding.CtrlModifier, Keybinding.Key("C")),
            ).render(),
        )
    }

    @Test
    fun `text transform`() {
        val out = readParts("layerdocs/texttransform.html")

        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(
                    size = TextTransformData.Size.LARGE,
                    style = TextTransformData.Style.ITALIC,
                    decoration = TextTransformData.Decoration.STRIKETHROUGH,
                ),
                children = buildInline { text("Foo") },
            ).render(),
        )

        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(
                    size = TextTransformData.Size.TINY,
                    weight = TextTransformData.Weight.BOLD,
                    decoration = TextTransformData.Decoration.UNDEROVERLINE,
                    variant = TextTransformData.Variant.SMALL_CAPS,
                ),
                children =
                    buildInline {
                        emphasis { text("Foo") }
                        text("bar")
                    },
            ).render(),
        )

        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(
                    case = TextTransformData.Case.CAPITALIZE,
                    decoration = TextTransformData.Decoration.ALL,
                    color = Color(255, 0, 0),
                ),
                children = buildInline { text("Foo") },
            ).render(),
        )

        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(),
                children = buildInline { text("Foo") },
            ).render(),
        )

        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(size = TextTransformData.Size.LARGE),
                className = "custom-class",
                children = buildInline { text("Foo") },
            ).render(),
        )

        // Subscript.
        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(script = TextTransformData.Script.SUB),
                children = buildInline { text("2") },
            ).render(),
        )

        // Superscript with additional style.
        assertEquals(
            out.next(),
            TextTransform(
                TextTransformData(
                    script = TextTransformData.Script.SUP,
                    weight = TextTransformData.Weight.BOLD,
                ),
                children = buildInline { text("2") },
            ).render(),
        )
    }

    @Test
    fun icon() {
        assertEquals("<i class=\"icon-image bi bi-alarm\" aria-hidden=\"true\">\n</i>", IconImage("alarm").render())
        assertEquals("<i class=\"icon-image bi bi-1-circle\" aria-hidden=\"true\">\n</i>", IconImage("1-circle").render())
    }

    @Test
    fun `last heading`() {
        val out = readParts("layerdocs/lastheading.html")

        assertEquals(
            out.next(),
            LastHeading(depth = 3).render(),
        )
    }

    @Test
    fun `file tree`() {
        val out = readParts("layerdocs/filetree.html")

        // Files only.
        assertEquals(
            out.next(),
            FileTree(
                listOf(
                    FileTreeEntry.File("file1.txt"),
                    FileTreeEntry.File("file2.json"),
                ),
            ).render(),
        )

        // Directory with files.
        assertEquals(
            out.next(),
            FileTree(
                listOf(
                    FileTreeEntry.Directory(
                        "src",
                        listOf(
                            FileTreeEntry.File("main.ts"),
                            FileTreeEntry.File("utils.ts"),
                        ),
                    ),
                    FileTreeEntry.File("README.md"),
                ),
            ).render(),
        )

        // Ellipsis.
        assertEquals(
            out.next(),
            FileTree(
                listOf(
                    FileTreeEntry.File("index.ts"),
                    FileTreeEntry.Ellipsis(),
                ),
            ).render(),
        )

        // Highlighted entries.
        assertEquals(
            out.next(),
            FileTree(
                listOf(
                    FileTreeEntry.File("file1.txt"),
                    FileTreeEntry.File("file2.txt", highlighted = true),
                    FileTreeEntry.Directory(
                        "src",
                        listOf(
                            FileTreeEntry.File("main.ts", highlighted = true),
                            FileTreeEntry.File("utils.ts"),
                        ),
                        highlighted = true,
                    ),
                    FileTreeEntry.Ellipsis(highlighted = true),
                ),
            ).render(),
        )
    }

    @Test
    fun bibliography() {
        val out = readParts("layerdocs/bibliography.html")

        val entries = listOf("einstein", "latexcompanion", "knuthwebsite")

        // Stub style producing simple, predictable output for HTML structure verification.
        val stubStyle =
            object : BibliographyStyle {
                override val name = "test"

                override val labelProvider =
                    object : BibliographyEntryLabelProviderStrategy {
                        override fun getCitationLabel(entries: List<BibliographyEntry>) = ""

                        override fun getListLabel(
                            entry: BibliographyEntry,
                            index: Int,
                        ) = "[${index + 1}]"
                    }

                override fun contentOf(entry: BibliographyEntry) = buildInline { text("Content of ${entry.citationKey}.") }
            }

        assertEquals(
            out.next(),
            AstRoot(
                listOf(
                    Heading(depth = 1, text = buildInline { text("Bibliography") }),
                    BibliographyView(
                        bibliography = Bibliography(entries.associateWith { BibliographyEntry(it) }),
                        style = stubStyle,
                    ),
                ),
            ).render(),
        )
    }
}
