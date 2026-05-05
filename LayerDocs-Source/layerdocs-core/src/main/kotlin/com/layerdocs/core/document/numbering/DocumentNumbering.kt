package com.layerdocs.core.document.numbering

import com.quarkdown.amber.annotations.Mergeable
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.block.FootnoteDefinition
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.base.inline.ReferenceFootnote
import com.layerdocs.core.ast.layerdocs.block.Math

/**
 * An immutable group of [NumberingFormat]s for different types of elements ([Node]s) in a document.
 * @param headings format for [Heading]s
 * @param figures format for [Figure]s
 * @param tables format for [Table]s
 * @param math format for [Math] blocks
 * @param codeBlocks format for [Code] blocks
 * @param footnotes format for [FootnoteDefinition] and [ReferenceFootnote]s
 * @param extra extra, dynamic formats for custom elements (e.g. [com.layerdocs.core.ast.layerdocs.block.Numbered])
 */
@Mergeable
data class DocumentNumbering(
    val headings: NumberingFormat? = null,
    val figures: NumberingFormat? = null,
    val tables: NumberingFormat? = null,
    val math: NumberingFormat? = null,
    val codeBlocks: NumberingFormat? = null,
    val footnotes: NumberingFormat? = null,
    val extra: Map<String, NumberingFormat> = emptyMap(),
)
