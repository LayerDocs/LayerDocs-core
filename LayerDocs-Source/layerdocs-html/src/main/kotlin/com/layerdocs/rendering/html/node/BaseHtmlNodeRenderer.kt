package com.layerdocs.rendering.html.node

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.attributes.id.getId
import com.layerdocs.core.ast.attributes.reference.getDefinition
import com.layerdocs.core.ast.base.block.BlankNode
import com.layerdocs.core.ast.base.block.BlockQuote
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.base.block.FootnoteDefinition
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.HorizontalRule
import com.layerdocs.core.ast.base.block.Html
import com.layerdocs.core.ast.base.block.LinkDefinition
import com.layerdocs.core.ast.base.block.Newline
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.base.block.getFormattedIndex
import com.layerdocs.core.ast.base.block.getIndex
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.ListItemVariantVisitor
import com.layerdocs.core.ast.base.block.list.OrderedList
import com.layerdocs.core.ast.base.block.list.TaskListItemVariant
import com.layerdocs.core.ast.base.block.list.UnorderedList
import com.layerdocs.core.ast.base.inline.CheckBox
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.Comment
import com.layerdocs.core.ast.base.inline.Emphasis
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.base.inline.LineBreak
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.ReferenceFootnote
import com.layerdocs.core.ast.base.inline.ReferenceImage
import com.layerdocs.core.ast.base.inline.Strikethrough
import com.layerdocs.core.ast.base.inline.Strong
import com.layerdocs.core.ast.base.inline.StrongEmphasis
import com.layerdocs.core.ast.base.inline.SubdocumentLink
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.ast.base.inline.getSubdocument
import com.layerdocs.core.ast.layerdocs.FunctionCallNode
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyCitation
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.ast.layerdocs.block.Box
import com.layerdocs.core.ast.layerdocs.block.Clipped
import com.layerdocs.core.ast.layerdocs.block.Collapse
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.ast.layerdocs.block.Figure
import com.layerdocs.core.ast.layerdocs.block.FileTree
import com.layerdocs.core.ast.layerdocs.block.Landscape
import com.layerdocs.core.ast.layerdocs.block.Math
import com.layerdocs.core.ast.layerdocs.block.MermaidDiagram
import com.layerdocs.core.ast.layerdocs.block.NavigationContainer
import com.layerdocs.core.ast.layerdocs.block.Numbered
import com.layerdocs.core.ast.layerdocs.block.PageBreak
import com.layerdocs.core.ast.layerdocs.block.SlidesFragment
import com.layerdocs.core.ast.layerdocs.block.SlidesSpeakerNote
import com.layerdocs.core.ast.layerdocs.block.Stacked
import com.layerdocs.core.ast.layerdocs.block.SubdocumentGraph
import com.layerdocs.core.ast.layerdocs.block.list.FocusListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.LocationTargetListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.TableOfContentsItemVariant
import com.layerdocs.core.ast.layerdocs.block.toc.TableOfContentsView
import com.layerdocs.core.ast.layerdocs.inline.IconImage
import com.layerdocs.core.ast.layerdocs.inline.InlineCollapse
import com.layerdocs.core.ast.layerdocs.inline.Keybinding
import com.layerdocs.core.ast.layerdocs.inline.LastHeading
import com.layerdocs.core.ast.layerdocs.inline.MathSpan
import com.layerdocs.core.ast.layerdocs.inline.PageCounter
import com.layerdocs.core.ast.layerdocs.inline.TextSymbol
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.Whitespace
import com.layerdocs.core.ast.layerdocs.invisible.PageMarginContentInitializer
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberFormatter
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberReset
import com.layerdocs.core.ast.layerdocs.invisible.SlidesConfigurationInitializer
import com.layerdocs.core.ast.layerdocs.reference.CrossReference
import com.layerdocs.core.context.Context
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.document.sub.getOutputFileName
import com.layerdocs.core.rendering.UnsupportedRenderException
import com.layerdocs.core.rendering.tag.TagNodeRenderer
import com.layerdocs.core.rendering.tag.buildMultiTag
import com.layerdocs.core.rendering.tag.buildTag
import com.layerdocs.core.rendering.tag.tagBuilder
import com.layerdocs.core.util.Escape
import com.layerdocs.core.util.node.toPlainText
import com.layerdocs.rendering.html.HtmlIdentifierProvider
import com.layerdocs.rendering.html.HtmlTagBuilder
import com.layerdocs.rendering.html.css.asCSS

/**
 * A renderer for vanilla Markdown ([com.layerdocs.core.flavor.base.BaseMarkdownFlavor]) nodes that exports their content into valid HTML code.
 * @param context additional information produced by the earlier stages of the pipeline
 */
open class BaseHtmlNodeRenderer(
    context: Context,
) : TagNodeRenderer<HtmlTagBuilder>(context),
    // Along with nodes, this component is also responsible for rendering list item variants.
    // For instance, a checked/unchecked task of attached to a list item.
    // These flavors directly affect the behavior of the HTML list item builder.
    ListItemVariantVisitor<HtmlTagBuilder.() -> Unit> {
    override fun createBuilder(
        name: String,
        pretty: Boolean,
    ) = HtmlTagBuilder(name, renderer = this, pretty)

    override fun escapeCriticalContent(unescaped: String) = Escape.Html.escape(unescaped)

    /**
     * @return the relative path to follow to get from the location of the current subdocument to the root
     *         (the location where the main HTML file is located, alongside `script`, `theme`, etc.).
     *         Subdocuments are exported flatly, so the path is either `.` for the root subdocument or `..` for non-root subdocuments.
     */
    private fun getPathToRoot(): String =
        when (this.context.subdocument) {
            Subdocument.Root -> "."
            else -> ".."
        }

    override fun createMediaPassthroughPrefixReplacement(): String = getPathToRoot()

    // Root

    override fun visit(node: AstRoot) =
        buildMultiTag {
            +node.children
        }

    // Block

    override fun visit(node: Newline) = ""

    override fun visit(node: Code) =
        buildTag("pre") {
            tag("code") {
                +escapeCriticalContent(node.content)

                classNames(
                    // Sets the code language.
                    node.language?.let { "language-$it" },
                    // Disables syntax highlighting.
                    "no-highlight".takeUnless { node.highlight },
                    // Disables line numbers.
                    "nohljsln".takeUnless { node.showLineNumbers },
                    // Focuses certain lines.
                    "focus-lines".takeIf { node.focusedLines != null },
                )

                // Focus range.
                optionalAttribute("data-focus-start", node.focusedLines?.start)
                optionalAttribute("data-focus-end", node.focusedLines?.end)
            }
        }

    override fun visit(node: HorizontalRule) =
        tagBuilder("hr")
            .void(true)
            .build()

    override fun visit(node: Heading) = buildTag("h${node.depth}", node.text)

    override fun visit(node: LinkDefinition) = "" // Not rendered

    override fun visit(node: FootnoteDefinition): CharSequence {
        val index = node.getIndex(context) ?: return "" // The footnote is rendered only if it is linked to a reference
        val formattedIndex = node.getFormattedIndex(context) ?: return ""

        return buildTag("span") {
            className("footnote-definition")
            optionalAttribute("id", HtmlIdentifierProvider.of(this@BaseHtmlNodeRenderer).getId(node))
            optionalAttribute("data-footnote-index", index)

            tag("sup") {
                className("footnote-label")
                +formattedIndex
            }
            tag("span") {
                +node.text
            }
        }
    }

    override fun visit(node: OrderedList) =
        tagBuilder("ol", node.children)
            .optionalAttribute("start", node.startIndex.takeUnless { it == 1 })
            .build()

    override fun visit(node: UnorderedList) = buildTag("ul", node.children)

    // Appends the base content of a list item, following the loose/tight rendering rules (CommonMark 5.3).
    override fun visit(node: ListItem) =
        buildTag("li") {
            // Flavors are executed on this HTML builder.
            node.variants.forEach { it.accept(this@BaseHtmlNodeRenderer).invoke(this) }

            // Loose lists (or items not linked to a list for some reason) are rendered as-is.
            if (node.owner?.isLoose != false) {
                // This base builder is empty by default.
                // If any of the variants added some content (e.g. a task checkbox),
                // the actual content is wrapped in a container for more convenient styling.
                when {
                    this.isEmpty -> +node.children
                    else -> +buildTag("div", node.children)
                }
                return@buildTag
            }
            // Tight lists don't wrap paragraphs in <p> tags (CommonMark 5.3).
            node.children.forEach {
                when (it) {
                    is Paragraph -> +it.text
                    else -> +it
                }
            }
        }

    // GFM 5.3 extension.
    override fun visit(variant: TaskListItemVariant): HtmlTagBuilder.() -> Unit =
        {
            className("task-list-item")
            +visit(CheckBox(variant.isChecked))
        }

    override fun visit(node: Html) = node.content

    /**
     * Table tag builder, enhanceable by subclasses.
     */
    protected fun tableBuilder(node: Table): HtmlTagBuilder =
        tagBuilder("table") {
            // Tables are stored by columns and here transposed to a row-based structure.
            val header = tag("thead")
            val headerRow = header.tag("tr")
            val body = tag("tbody")
            val bodyRows = mutableListOf<HtmlTagBuilder>()

            node.columns.forEach { column ->
                // Value to assign to the 'align' attribute for each cell of this column.
                val alignment = column.alignment.takeUnless { it == Table.Alignment.NONE }?.asCSS

                // Header cell.
                headerRow
                    .tag("th", column.header.text)
                    .optionalAttribute("align", alignment)

                // Body cells.
                column.cells.forEachIndexed { index, cell ->
                    // Adding a new row if needed.
                    if (index >= bodyRows.size) {
                        bodyRows += body.tag("tr")
                    }
                    // Adding a cell.
                    bodyRows[index]
                        .tag("td", cell.text)
                        .optionalAttribute("align", alignment)
                }
            }
        }

    override fun visit(node: Table) = tableBuilder(node).build()

    override fun visit(node: Paragraph) = buildTag("p", node.text)

    override fun visit(node: BlockQuote) = buildTag("blockquote", node.content)

    override fun visit(node: BlankNode) = "" // Fallback block, should not happen

    // Inline

    override fun visit(node: Comment) = "" // Ignored

    override fun visit(node: LineBreak) =
        tagBuilder("br")
            .void(true)
            .build()

    private fun buildLinkTag(node: Link): HtmlTagBuilder =
        tagBuilder("a", node.label)
            .attribute("href", node.url)
            .optionalAttribute("title", node.title?.toPlainText(renderer = this))

    override fun visitTransformed(node: Link) = buildLinkTag(node).build()

    override fun visit(node: SubdocumentLink): CharSequence {
        val subdocument: Subdocument =
            node.getSubdocument(context)
                ?: return "[???]"

        val isCurrentSubdocument = subdocument == this.context.subdocument

        val url =
            buildString {
                append(getPathToRoot())
                append("/")
                append(subdocument.getOutputFileName(context))
                node.anchor?.let { anchor ->
                    append("#")
                    append(anchor)
                }
            }

        return buildLinkTag(node.link.copy(url = url))
            .optionalAttribute("aria-current", "page".takeIf { isCurrentSubdocument })
            .build()
    }

    override fun visit(node: ReferenceFootnote): CharSequence {
        val definition: FootnoteDefinition =
            node.getDefinition(context)
                ?: return node.fallback().accept(this)

        return buildTag("sup") {
            classNames("footnote-reference", "footnote-label")
            val definitionId = HtmlIdentifierProvider.of(this@BaseHtmlNodeRenderer).getId(definition)
            attribute("data-definition", definitionId)
            tag("a") {
                optionalAttribute("href", "#$definitionId")
                +(definition.getFormattedIndex(context) ?: "?")
            }
        }
    }

    override fun visitTransformed(node: Image): CharSequence =
        tagBuilder("img")
            .attribute("src", node.link.url)
            .attribute("alt", node.link.label.toPlainText(renderer = this)) // Emphasis is discarded (CommonMark 6.4)
            .optionalAttribute("title", node.link.title?.toPlainText(renderer = this))
            .style {
                "width" value node.width
                "height" value node.height
            }.void(true)
            .build()

    override fun visit(node: ReferenceImage): CharSequence {
        val link = node.link.getDefinition(context) ?: return node.link.fallback().accept(this)
        return Image(link, node.width, node.height, node.referenceId).accept(this)
    }

    override fun visit(node: CheckBox) =
        tagBuilder("input") {}
            .attribute("disabled", "")
            .attribute("type", "checkbox")
            .optionalAttribute("checked", "".takeIf { node.isChecked })
            .void(true)
            .build()

    override fun visit(node: Text) = node.text

    override fun visit(node: TextSymbol) = Escape.Html.escape(node.text) // e.g. © -> &copy;

    override fun visit(node: CodeSpan) = buildTag("code", escapeCriticalContent(node.text))

    override fun visit(node: Emphasis) = buildTag("em", node.children)

    override fun visit(node: Strong) = buildTag("strong", node.children)

    override fun visit(node: StrongEmphasis) =
        buildTag("em") {
            tag("strong") {
                +node.children
            }
        }

    override fun visit(node: Strikethrough) = buildTag("del", node.children)

    // LayerDocs - implemented by LayerDocsHtmlNodeRenderer

    override fun visit(node: FunctionCallNode): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Figure<*>): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: PageBreak): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Math): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Container): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Stacked): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Numbered): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Landscape): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Clipped): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Box): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Collapse): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Whitespace): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: NavigationContainer): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: TableOfContentsView): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: BibliographyView): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: MermaidDiagram): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: FileTree): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: SubdocumentGraph): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: PageMarginContentInitializer): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: PageNumberFormatter): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: PageNumberReset): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: PageCounter): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: LastHeading): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: SlidesConfigurationInitializer): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: MathSpan): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: TextTransform): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: IconImage): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: InlineCollapse): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: Keybinding): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: CrossReference): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: BibliographyCitation): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: SlidesFragment): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(node: SlidesSpeakerNote): CharSequence = throw UnsupportedRenderException(node)

    override fun visit(variant: FocusListItemVariant): HtmlTagBuilder.() -> Unit = throw UnsupportedRenderException(variant::class)

    override fun visit(variant: LocationTargetListItemVariant): HtmlTagBuilder.() -> Unit = throw UnsupportedRenderException(variant::class)

    override fun visit(variant: TableOfContentsItemVariant): HtmlTagBuilder.() -> Unit = throw UnsupportedRenderException(variant::class)
}
