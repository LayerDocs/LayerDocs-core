package com.layerdocs.core.ast.dsl

import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.ListItemVariant

/**
 * A builder of list items.
 * @see BlockAstBuilder.orderedList
 * @see BlockAstBuilder.unorderedList
 */
class ListAstBuilder : AstBuilder() {
    /**
     * @see ListItem
     */
    fun listItem(
        vararg variants: ListItemVariant,
        block: BlockAstBuilder.() -> Unit,
    ) = node(ListItem(variants.toList(), buildBlocks(block)))
}
