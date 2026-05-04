package com.layerdocs.stdlib

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.InlineMarkdownContent
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.createSectionHeading
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyCitation
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.bibliography.style.csl.CslBibliographyStyle
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.library.module.moduleOf
import com.layerdocs.core.function.reflect.annotation.Injected
import com.layerdocs.core.function.reflect.annotation.LikelyNamed
import com.layerdocs.core.function.reflect.annotation.Name
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.wrappedAsValue
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.util.trimEntries

/**
 * `Bibliography` stdlib module exporter.
 * This module handles bibliographies and citations.
 * @see com.layerdocs.core.bibliography.Bibliography
 */
val Bibliography: LayerDocsModule =
    moduleOf(
        ::bibliography,
        ::cite,
    )

/**
 * The default [CSL](https://citationstyles.org) style used when no explicit style is specified.
 */
private const val DEFAULT_CSL_STYLE = "ieee"

/**
 * Generates a bibliography from a bibliography file.
 *
 * Supported formats include [BibTeX](https://www.bibtex.org) (`.bib`),
 * CSL JSON, YAML, EndNote, and RIS.
 *
 * The bibliography is formatted using a [CSL](https://citationstyles.org) style definition,
 * powered by [citeproc-java](https://github.com/michel-kraemer/citeproc-java).
 * This enables support for a curated selection of citation styles from the
 * [CSL Style Repository](https://github.com/citation-style-language/styles).
 *
 * Example:
 * ```markdown
 * .bibliography {bibliography.bib} style:{apa}
 * ```
 *
 * @param path path to the bibliography file, with extension
 * @param style [CSL](https://citationstyles.org) style identifier (e.g. `apa`, `ieee`, `chicago-author-date`)
 *              from LayerDocs's selection. See the wiki page for a list of supported styles.
 * @param title title of the bibliography. If unset, the default localized title is used
 * @param breakPage whether the heading preceding the bibliography triggers an automatic page break.
 *                  Enabled by default.
 * @param headingDepth depth of the heading preceding the bibliography
 * @param trackHeadingLocation whether the heading preceding the bibliography should be numbered
 *                             and have its position tracked in the document hierarchy.
 *                             Implicitly enabled when [indexHeading] is enabled.
 * @param indexHeading whether the heading preceding the bibliography should itself be indexed
 *                     in the document's table of contents.
 * @return an [AstRoot] containing an optional heading and a [BibliographyView]
 * @see cite to cite bibliography entries
 * @throws java.io.IOException if the bibliography file cannot be read or parsed
 * @throws IllegalArgumentException if the specified style does not exist or is invalid
 * @permission [Permission.ProjectRead] to read bibliography files located in the project directory
 * @permission [Permission.GlobalRead] to read bibliography files located outside the project directory
 * @wiki bibliography
 */
fun bibliography(
    @Injected context: Context,
    path: String,
    @LikelyNamed style: String = DEFAULT_CSL_STYLE,
    @LikelyNamed title: InlineMarkdownContent? = null,
    @Name("breakpage") breakPage: Boolean = true,
    @Name("headingdepth") headingDepth: Int = 1,
    @Name("numberheading") trackHeadingLocation: Boolean = false,
    @Name("indexheading") indexHeading: Boolean = false,
): NodeValue {
    val file = file(context, path)
    val resolvedStyle = CslBibliographyStyle.from(style, file.inputStream(), file.name, context.documentInfo.locale)

    val heading =
        Heading.createSectionHeading(
            title?.children,
            localizationKey = "bibliography",
            context,
            depth = headingDepth,
            canBreakPage = breakPage,
            canTrackLocation = trackHeadingLocation,
            includeInTableOfContents = indexHeading,
        )

    return AstRoot(
        listOfNotNull(
            heading,
            BibliographyView(
                bibliography = resolvedStyle.bibliography,
                style = resolvedStyle,
            ),
        ),
    ).wrappedAsValue()
}

/**
 * Creates a citation to one or more bibliography entries.
 *
 * The result is a label that matches with that of the bibliography entries with the given [key].
 * Multiple keys can be specified as a comma-separated list,
 * producing a single combined label (e.g. `[1], [3]` or `(Einstein, 1905; Hawking, 1988)`).
 *
 * Example:
 *
 * `bibliography.bib`
 *
 * ```bibtex
 * @article{einstein,
 *   ...
 * }
 *
 * ...
 * ```
 *
 * LayerDocs:
 *
 * ```markdown
 * Einstein's work .cite {einstein} is fundamental to modern physics.
 *
 * These results .cite {einstein, latexcompanion} are well-known.
 *
 * .bibliography {bibliography.bib}
 * ```
 *
 * Result:
 * ```text
 * Einstein's work [1] is fundamental to modern physics.
 *
 * These results [1], [2] are well-known.
 * ```
 * @param key the key (or comma-separated keys) of the bibliography entries to cite
 * @return a wrapped [BibliographyCitation] node
 * @throws IllegalArgumentException if no non-blank citation key is provided
 * @wiki bibliography#citations
 */
fun cite(key: String): NodeValue {
    val keys = key.split(",").trimEntries()

    require(keys.isNotEmpty()) { "At least one citation key must be specified." }

    return BibliographyCitation(keys).wrappedAsValue()
}
