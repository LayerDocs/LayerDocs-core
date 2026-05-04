package com.layerdocs.core.ast.attributes.localization

/**
 * Keys for localization of kinds of nodes,
 * used to look up localized strings in the default [com.layerdocs.core.localization.LocalizationTable].
 * @see LocalizedKind
 */
object LocalizedKindKeys {
    /**
     * @see com.layerdocs.core.ast.base.block.Code
     */
    const val CODE_BLOCK = "listing"

    /**
     * @see com.layerdocs.core.ast.layerdocs.block.Figure
     */
    const val FIGURE = "figure"

    /**
     * @see com.layerdocs.core.ast.base.block.Heading
     */
    const val HEADING = "section"

    /**
     * @see com.layerdocs.core.ast.base.block.Table
     */
    const val TABLE = "table"
}
