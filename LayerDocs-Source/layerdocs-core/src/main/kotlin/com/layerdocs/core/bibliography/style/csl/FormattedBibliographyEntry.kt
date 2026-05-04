package com.layerdocs.core.bibliography.style.csl

import com.layerdocs.core.ast.InlineContent

/**
 * A fully formatted bibliography entry produced by the CSL processor.
 * @param label the entry label (e.g. `[1]` for numbered styles, empty for author-year styles)
 * @param content the formatted entry content as LayerDocs AST nodes
 */
internal data class FormattedBibliographyEntry(
    val label: String,
    val content: InlineContent,
)
