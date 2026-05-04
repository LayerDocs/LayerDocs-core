package com.layerdocs.core.ast.layerdocs.block.list

import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.ListItemVariant
import com.layerdocs.core.ast.base.block.list.ListItemVariantVisitor
import com.layerdocs.core.context.toc.TableOfContents

/**
 * A list item variant that associates a [ListItem] to an item of a [TableOfContents],
 * such as a [com.layerdocs.core.ast.base.block.Heading].
 * @param item the ToC item associated with the list item
 */
data class TableOfContentsItemVariant(
    val item: TableOfContents.Item,
) : ListItemVariant {
    override fun <T> accept(visitor: ListItemVariantVisitor<T>) = visitor.visit(this)
}
