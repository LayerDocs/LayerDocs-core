package com.layerdocs.rendering.html.node

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.id.Identifiable
import com.layerdocs.core.ast.attributes.id.getId
import com.layerdocs.core.ast.attributes.localization.LocalizedKind
import com.layerdocs.core.ast.attributes.location.LocationTrackableNode
import com.layerdocs.core.ast.attributes.location.getLocationLabel
import com.layerdocs.core.ast.attributes.reference.getCitationLabel
import com.layerdocs.core.ast.attributes.reference.getDefinition
import com.layerdocs.core.ast.base.TextNode
import com.layerdocs.core.ast.base.block.BlockQuote
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.HorizontalRule
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.base.block.isMarker
import com.layerdocs.core.ast.base.block.list.ListBlock
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.CriticalContent
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.ast.layerdocs.CaptionableNode
import com.layerdocs.core.ast.layerdocs.FunctionCallNode
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyCitation
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.ast.layerdocs.block.Box
import com.layerdocs.core.ast.layerdocs.block.Clipped
import com.layerdocs.core.ast.layerdocs.block.Collapse
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.ast.layerdocs.block.Figure
import com.layerdocs.core.ast.layerdocs.block.FileTree
import com.layerdocs.core.ast.layerdocs.block.FileTreeEntry
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
import com.layerdocs.core.ast.layerdocs.block.toc.convertTableOfContentsToListNode
import com.layerdocs.core.ast.layerdocs.inline.IconImage
import com.layerdocs.core.ast.layerdocs.inline.InlineCollapse
import com.layerdocs.core.ast.layerdocs.inline.Keybinding
import com.layerdocs.core.ast.layerdocs.inline.LastHeading
import com.layerdocs.core.ast.layerdocs.inline.MathSpan
import com.layerdocs.core.ast.layerdocs.inline.PageCounter
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.TextTransformData
import com.layerdocs.core.ast.layerdocs.inline.Whitespace
import com.layerdocs.core.ast.layerdocs.invisible.PageMarginContentInitializer
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberFormatter
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberReset
import com.layerdocs.core.ast.layerdocs.invisible.SlidesConfigurationInitializer
import com.layerdocs.core.ast.layerdocs.reference.CrossReference
import com.layerdocs.core.ast.layerdocs.reference.CrossReferenceableNode
import com.layerdocs.core.ast.layerdocs.reference.linkableReferenceId
import com.layerdocs.core.context.Context
import com.layerdocs.core.context.localization.localizeOrNull
import com.layerdocs.core.context.options.shouldAutoPageBreak
import com.layerdocs.core.context.subdocument.subdocumentGraph
import com.layerdocs.core.document.layout.caption.CaptionPosition
import com.layerdocs.core.document.layout.caption.CaptionPositionInfo
import com.layerdocs.core.document.numbering.NumberingFormat
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.rendering.tag.buildMultiTag
import com.layerdocs.core.rendering.tag.buildTag
import com.layerdocs.core.rendering.tag.tagBuilder
import com.layerdocs.core.util.Escape
import com.layerdocs.core.util.kebabCaseName
import com.layerdocs.rendering.html.HtmlIdentifierProvider
import com.layerdocs.rendering.html.HtmlIdentifierProvider.Companion.sanitizeId
import com.layerdocs.rendering.html.HtmlTagBuilder
import com.layerdocs.rendering.html.css.CssBuilder
import com.layerdocs.rendering.html.css.asCSS

/**
 * A renderer for LayerDocs ([com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor]) nodes that exports their content into valid HTML code.
 * @param context additional information produced by the earlier stages of the pipeline
 */
class LayerDocsHtmlNodeRenderer(
    context: Context,
) : BaseHtmlNodeRenderer(context) {
    /**
     * A `<div class="styleClass">...</div>` tag.
     */
    private fun div(
        styleClass: String? = null,
        init: HtmlTagBuilder.() -> Unit,
    ) = tagBuilder("div", init = init)
        .className(styleClass)
        .build()

    /**
     * A `<div class="styleClass">children</div>` tag.
     */
    private fun div(
        styleClass: String,
        children: List<Node>,
    ) = div(styleClass) { +children }

    /**
     * Adds a `data-location` attribute to the location-trackable node, if its location is available.
     * The location is formatted according to [format].
     */
    private fun HtmlTagBuilder.withLocationLabel(node: LocationTrackableNode) =
        optionalAttribute(
            "data-location",
            node.getLocationLabel(context)?.takeUnless { it.isEmpty() },
        )

    /**
     * Adds a `data-localized-kind` attribute to the localizable node.
     * The kind name is localized according to the current locale.
     */
    private fun HtmlTagBuilder.withLocalizedKind(node: LocalizedKind) =
        optionalAttribute(
            "data-localized-kind",
            context.localizeOrNull(key = node.kindLocalizationKey),
        )

    /**
     * Retrieves the location-based label of the [node], displays an optional caption preceded by the label, and also applies the label as its ID.
     * The label is pre-formatted according to the current [NumberingFormat].
     *
     * At the end, thanks to injected CSS variables, the visible outcome is `<localized_kind> <label>: <caption>`.
     *
     * @param node node to display the caption, and apply the ID, for
     * @param captionTagName tag name of the caption element. E.g. "figcaption" for figures, "caption" for tables
     * @param idPrefix prefix for the ID. For instance, the prefix `figure` lets the ID be `figure-X.Y`, where `X.Y` is the label.
     * @param positionProvider position of the caption relative to the content
     * @see CaptionableNode
     * @see getLocationLabel to retrieve the numbered label
     */
    private fun <T> HtmlTagBuilder.numberedCaption(
        node: T,
        captionTagName: String = "figcaption",
        idPrefix: String = node.kindLocalizationKey,
        positionProvider: CaptionPositionInfo.() -> CaptionPosition?,
    ): HtmlTagBuilder where T : CaptionableNode, T : LocationTrackableNode, T : LocalizedKind =
        this.apply {
            val position =
                context.documentInfo.layout.captionPosition
                    .getOrDefault(positionProvider)

            // The reference ID or label is set as the ID of the element, allowing cross-references to link to it.
            val label = node.getLocationLabel(context)
            val id = (node as? CrossReferenceableNode)?.linkableReferenceId?.let(::sanitizeId) ?: label?.let { "$idPrefix-$it" }
            id?.let { optionalAttribute("id", it) }

            if (node.caption == null && label == null) {
                // No caption and no label: nothing to show.
                return@apply
            }

            +buildTag(captionTagName) {
                className("caption-${position.asCSS}")
                withLocationLabel(node)
                withLocalizedKind(node)

                node.caption?.let { +it }
            }
        }

    // LayerDocs node rendering

    // The function was already expanded by previous stages: its output nodes are stored in its children.
    override fun visit(node: FunctionCallNode): CharSequence = visit(AstRoot(node.children))

    // Block

    override fun visit(node: Figure<*>) =
        buildTag("figure") {
            +node.child
            numberedCaption(node, positionProvider = { figures })
        }

    // An empty div that acts as a page break.
    override fun visit(node: PageBreak) =
        tagBuilder("div")
            .className("page-break")
            .hidden()
            .build()

    override fun visit(node: HorizontalRule) =
        tagBuilder("hr")
            .className("page-break")
            .void(true)
            .build()

    override fun visit(node: Math) =
        buildTag("formula") {
            +node.expression
            attribute("data-block", "")
            optionalAttribute("id", node.linkableReferenceId?.let(::sanitizeId))
            optionalAttribute("data-location", node.getLocationLabel(context))
        }

    override fun visit(node: Container) =
        buildTag("div") {
            classNames(
                "container",
                "fullwidth".takeIf { node.fullWidth },
                "float".takeIf { node.float != null },
                "full-column-span".takeIf { node.fullColumnSpan },
                node.textTransform?.size?.asCSS,
                node.className,
            )

            +node.children

            style {
                "width" value node.width
                "height" value node.height
                "color" value node.foregroundColor
                "background-color" value node.backgroundColor
                "margin" value node.margin
                "padding" value node.padding
                "border-color" value node.borderColor
                "border-width" value node.borderWidth
                "border-radius" value node.cornerRadius

                "border-style" value
                    when {
                        // If the border style is set, it is used.
                        node.borderStyle != null -> node.borderStyle

                        // If border properties are set, a normal (solid) border is used.
                        node.borderColor != null || node.borderWidth != null -> Container.BorderStyle.NORMAL

                        // No border style.
                        else -> null
                    }

                "justify-items" value node.alignment
                "text-align" value node.textAlignment
                "float" value node.float
                node.textTransform?.let { textTransform(it) }
            }
        }

    override fun visit(node: Stacked) =
        div("stack stack-${node.layout.asCSS}") {
            +node.children

            style {
                (node.layout as? Stacked.Grid)?.let {
                    // The amount of 'auto' matches the amount of columns/rows.
                    "grid-template-columns" value "auto ".repeat(it.columnCount).trimEnd()
                }

                "justify-content" value node.mainAxisAlignment
                "align-items" value node.crossAxisAlignment
                "row-gap" value node.rowGap
                "column-gap" value node.columnGap
            }
        }

    override fun visit(node: Numbered): CharSequence {
        val id = node.linkableReferenceId?.let(::sanitizeId)
        return if (id != null) {
            buildTag("div") {
                attribute("id", id)
                +node.children
            }
        } else {
            buildMultiTag {
                +node.children
            }
        }
    }

    override fun visit(node: Landscape) = div("landscape", node.children)

    override fun visit(node: Clipped) =
        div("clip clip-${node.clip.asCSS}") {
            +Container(children = node.children)
        }

    override fun visit(node: Box) =
        div {
            classNames("box", node.type.asCSS)

            if (node.title != null) {
                tag("header") {
                    tag("h4", node.title!!)

                    style {
                        "color" value node.foregroundColor // Must be repeated to force override.
                        "padding" value node.padding
                    }
                }
            }

            // Box actual content.
            +div("box-content") {
                +node.content

                style { "padding" value node.padding }
            }

            // Box style. Padding is applied separately to the header and the content.
            style {
                "background-color" value node.backgroundColor
                "color" value node.foregroundColor
            }
        }

    override fun visit(node: Collapse) =
        buildTag("details") {
            if (node.isOpen) {
                attribute("open", "")
            }

            tag("summary") { +node.title }
            +node.content
        }

    override fun visit(node: Whitespace) =
        // If at least one of the dimensions is set, the square will have a fixed size.
        // Otherwise, a blank character is rendered.
        when {
            node.width == null && node.height == null -> {
                buildTag("span", "&nbsp;")
            }

            else -> {
                buildTag("div") {
                    style {
                        "width" value node.width
                        "height" value node.height
                    }
                }
            }
        }

    override fun visit(node: NavigationContainer) =
        buildTag("nav") {
            optionalAttribute("role", node.role?.asCSS)
            optionalAttribute("data-role", node.role?.kebabCaseName)
            +node.children
        }

    override fun visit(node: TableOfContentsView): CharSequence {
        val tableOfContents = context.attributes.tableOfContents ?: return ""

        val tree: ListBlock =
            convertTableOfContentsToListNode(
                node,
                this@LayerDocsHtmlNodeRenderer,
                tableOfContents.items,
                linkUrlMapper = { item ->
                    "#" + HtmlIdentifierProvider.of(this@LayerDocsHtmlNodeRenderer).getId(item.target)
                },
            )

        return NavigationContainer(
            role = NavigationContainer.Role.TABLE_OF_CONTENTS,
            listOf(tree),
        ).accept(this)
    }

    override fun visit(node: BibliographyView) =
        buildTag("div") {
            classNames("bibliography", "bibliography-${node.style.name}")
            node.bibliography.entries.values.mapIndexed { index, entry ->
                tag("span") {
                    className("bibliography-entry-label")
                    +node.style.labelProvider.getListLabel(entry, index)
                }
                tag("span") {
                    className("bibliography-entry-content")
                    +node.style.contentOf(entry)
                }
            }
        }

    override fun visit(node: MermaidDiagram) =
        buildTag("pre") {
            classNames("mermaid")
            +escapeCriticalContent(node.code)
        }

    override fun visit(node: FileTree): CharSequence =
        buildTag("div") {
            className("file-tree")

            fun buildEntries(entries: List<FileTreeEntry>): CharSequence =
                buildTag("ul") {
                    entries.forEach { entry ->
                        tag("li") {
                            when (entry) {
                                is FileTreeEntry.File -> {
                                    className("file")
                                    attribute("data-name", escapeCriticalContent(entry.name))
                                    +CriticalContent(entry.name)
                                }

                                is FileTreeEntry.Directory -> {
                                    className("directory")
                                    attribute("data-name", escapeCriticalContent(entry.name))
                                    +CriticalContent(entry.name)
                                    +buildEntries(entry.entries)
                                }

                                is FileTreeEntry.Ellipsis -> {
                                    className("ellipsis")
                                    +"&hellip;"
                                }
                            }
                            if (entry.highlighted) {
                                attribute("data-highlighted", "")
                            }
                        }
                    }
                }
            +buildEntries(node.entries)
        }

    override fun visit(node: SubdocumentGraph): CharSequence {
        fun id(subdocument: Subdocument) = subdocument.name.hashCode()

        val content =
            "graph LR\n" +
                context.subdocumentGraph.edges.joinToString("\n") { edge ->
                    val from = edge.first
                    val to = edge.second
                    val (idFrom, idTo) = id(from) to id(to)
                    val (nameFrom, nameTo) = from.name to to.name

                    "$idFrom[\"$nameFrom\"] --> $idTo[\"$nameTo\"]"
                }

        return MermaidDiagram(content).accept(this)
    }

    // Inline

    override fun visit(node: MathSpan) = buildTag("formula", node.expression)

    override fun visit(node: CrossReference): CharSequence {
        val definition: CrossReferenceableNode = node.getDefinition(context) ?: return Text("[???]").accept(this)

        // The target node could have an ID. If so, the reference is a link to that node.
        // Headings use Identifiable for auto-generated IDs; other nodes use their referenceId directly.
        val anchorId =
            (definition as? Identifiable)?.accept(HtmlIdentifierProvider.of(this))
                ?: definition.linkableReferenceId?.let(::sanitizeId)

        val reference =
            buildTag("span") {
                className("cross-reference")

                when (definition) {
                    is LocationTrackableNode if definition.getLocationLabel(context) != null -> {
                        withLocationLabel(definition)
                    }

                    // If no label is available, use the caption if possible.
                    is CaptionableNode if definition.caption != null -> {
                        +definition.caption!!
                    }

                    // Fallback: use the target's text if possible.
                    is TextNode -> {
                        +definition.text
                    }

                    // Fallback: raw reference ID, escaped for safe HTML output.
                    else -> {
                        +escapeCriticalContent(node.referenceId)
                    }
                }
                if (definition is LocalizedKind) {
                    withLocalizedKind(definition)
                }
            }

        return when (anchorId) {
            null -> {
                reference
            }

            // No linkable ID.
            else -> {
                buildTag("a") {
                    // ID available: link to the target.
                    attribute("href", "#$anchorId")
                    +reference
                }
            }
        }
    }

    override fun visit(node: BibliographyCitation): CharSequence {
        val label = node.getCitationLabel(context) ?: return Text("[???]").accept(this)
        return Text(label).accept(this)
    }

    override fun visit(node: SlidesFragment) =
        tagBuilder("div", node.children)
            .classNames("fragment", node.behavior.asCSS)
            .build()

    override fun visit(node: SlidesSpeakerNote) =
        buildTag("aside") {
            className("notes")
            hidden()
            +node.children
        }

    /**
     * Applies the text transformation of [data] into [this] CSS builder.
     */
    private fun CssBuilder.textTransform(data: TextTransformData) {
        "font-weight" value data.weight
        "font-style" value data.style
        "font-variant" value data.variant
        "text-decoration" value data.decoration
        "text-transform" value data.case
        "color" value data.color
    }

    override fun visit(node: TextTransform): CharSequence {
        val tagName =
            when (node.data.script) {
                TextTransformData.Script.SUB -> "sub"
                TextTransformData.Script.SUP -> "sup"
                null -> "span"
            }

        return buildTag(tagName) {
            classNames(
                node.data.size?.asCSS, // e.g. 'size-small' class
                node.className,
            )
            +node.children
            style { textTransform(node.data) }
        }
    }

    override fun visit(node: IconImage): CharSequence =
        buildTag("i") {
            val name = Escape.Html.escape(node.name)
            classNames("icon-image", "bi", "bi-$name")
            attribute("aria-hidden", "true")
        }

    override fun visit(node: InlineCollapse) =
        buildTag("span") {
            // Dynamic behavior is handled by JS.
            className("inline-collapse")
            attribute("data-full-text", buildMultiTag { +node.text })
            attribute("data-collapsed-text", buildMultiTag { +node.placeholder })
            attribute("data-collapsed", !node.isOpen)
            +if (node.isOpen) node.text else node.placeholder
        }

    override fun visit(node: Keybinding) =
        buildTag("span") {
            className("keybinding")
            node.parts.forEach { part ->
                +buildTag("kbd") {
                    +part.displayName
                    optionalAttribute(
                        "data-mac",
                        part.macDisplayName.takeIf { it != part.displayName },
                    )
                }
            }
        }

    // Invisible nodes

    override fun visit(node: PageMarginContentInitializer) =
        // In slides and paged documents, these elements are copied to each page in post-processing.
        buildTag("div") {
            classNames(
                "page-margin-content",
                "page-margin-${node.position.asCSS}",
            )
            attribute("data-on-left-page", node.position.forLeftPage.asCSS)
            attribute("data-on-right-page", node.position.forRightPage.asCSS)
            +node.children
        }

    override fun visit(node: PageNumberReset) =
        buildTag("div") {
            className("page-number-reset")
            attribute("data-start", node.startFrom)
            hidden()
        }

    override fun visit(node: PageNumberFormatter) =
        buildTag("div") {
            className("page-number-formatter")
            attribute("data-format", node.format)
            hidden()
        }

    override fun visit(node: PageCounter) =
        // The current or total page number.
        // The actual number is filled by a script at runtime
        // (either slides.js or paged.js, depending on the document type).
        buildTag("span") {
            +"-" // The default placeholder in case it is not filled by a script (e.g. plain documents).
            className(
                when (node.target) {
                    PageCounter.Target.CURRENT -> "current-page-number"
                    PageCounter.Target.TOTAL -> "total-page-number"
                },
            )
        }

    override fun visit(node: LastHeading) =
        buildTag("span") {
            // Since pagination is performed at runtime, the last heading must be retrieved at runtime as well.
            className("last-heading")
            attribute("data-depth", node.depth)
        }

    override fun visit(node: SlidesConfigurationInitializer): CharSequence =
        buildTag("script") {
            hidden()
            // Injects properties that are read at runtime after the document is loaded.
            +buildString {
                append("window.slidesConfig = {")
                node.centerVertically?.let {
                    append("center: $it,")
                }
                node.showControls?.let {
                    append("showControls: $it,")
                }
                node.showNotes?.let {
                    append("showNotes: $it,")
                }
                node.transition?.let {
                    append("transitionStyle: '${it.style.asCSS}',")
                    append("transitionSpeed: '${it.speed.asCSS}',")
                }
                append("};")
            }
        }

    // Additional behavior of base nodes

    // On top of the default behavior, an anchor ID is set,
    // and it could force an automatic page break if suitable.
    override fun visit(node: Heading): String {
        val tagBuilder =
            when {
                // When a heading has a depth of 0 (achievable only via functions), it is an invisible marker with an ID.
                node.isMarker -> {
                    tagBuilder("div") {
                        className("marker")
                        hidden()
                    }
                }

                // Regular headings.
                else -> {
                    tagBuilder("h${node.depth}", node.text)
                }
            }

        // The heading tag itself.
        return tagBuilder
            .className("page-break".takeIf { context.shouldAutoPageBreak(node) })
            .optionalAttribute(
                "id",
                // Generate an automatic identifier if allowed by settings.
                HtmlIdentifierProvider
                    .of(renderer = this)
                    .takeIf { context.options.enableAutomaticIdentifiers || node.customId != null }
                    ?.getId(node),
            ).optionalAttribute("data-decorative", "".takeIf { node.isDecorative })
            .withLocationLabel(node)
            .build()
    }

    // On top of the base behavior, a blockquote can have a type and an attribution.
    override fun visit(node: BlockQuote) =
        buildTag("blockquote") {
            // If the quote has a type (e.g. TIP),
            // the whole quote is marked as a 'tip' blockquote
            // and a localized label is shown (e.g. 'Tip:' for English).
            node.type?.asCSS?.let { type ->
                className(type)
                // The type is associated to a localized label
                // only if the document language is set and the set language is supported.
                context.localizeOrNull(key = type)?.let { localizedLabel ->
                    // The localized label is set as a CSS variable.
                    // Themes can customize label appearance and formatting.
                    style { "--quote-type-label" value "'$localizedLabel'" }
                    // The quote is marked as labeled to allow further customization.
                    attribute("data-labeled", "")
                }
            }

            // If the quote has a type, the first child must be a paragraph, because the label is rendered as ::before.
            if (node.type != null && node.content.firstOrNull() !is Paragraph) {
                +tagBuilder("p").acceptEmpty().build()
            }

            +node.content
            node.attribution?.let {
                +tagBuilder("p", it).className("attribution").build()
            }
        }

    // LayerDocs introduces table captions, also numerated.
    override fun visit(node: Table) =
        super
            .tableBuilder(node)
            .apply {
                numberedCaption(
                    node,
                    captionTagName = "caption",
                    positionProvider = { tables },
                )
            }.build()

    override fun visit(node: Code): String {
        val block = super.visit(node)

        // If the code is numbered, has a caption, or has a reference ID, it is wrapped in a figure.
        if (node.caption == null && node.getLocationLabel(context) == null && node.linkableReferenceId == null) {
            return block
        }
        return buildTag("figure") {
            +block
            numberedCaption(node, positionProvider = { codeBlocks })
        }
    }

    // A code span can contain additional content, such as a color preview.
    override fun visit(node: CodeSpan): String {
        val codeTag = super.visit(node)

        // The code is wrapped to allow additional content.
        return buildTag("span") {
            className("codespan-content")

            +codeTag

            when (val content = node.content) {
                null -> {}

                // No additional content.
                is CodeSpan.ColorContent -> {
                    // If the code contains a color code, show the color preview.
                    +buildTag("span") {
                        style { "background-color" value content.color }
                        className("color-preview")
                    }
                }
            }
        }
    }

    // List item variants.

    override fun visit(variant: FocusListItemVariant): HtmlTagBuilder.() -> Unit =
        {
            if (variant.isFocused) {
                className("focused")
            }
        }

    override fun visit(variant: LocationTargetListItemVariant): HtmlTagBuilder.() -> Unit = { withLocationLabel(variant.target) }

    override fun visit(variant: TableOfContentsItemVariant): HtmlTagBuilder.() -> Unit =
        {
            attribute(
                "data-target-id",
                HtmlIdentifierProvider.of(this@LayerDocsHtmlNodeRenderer).getId(variant.item.target),
            )
            attribute("data-depth", variant.item.depth.toString())
        }
}
