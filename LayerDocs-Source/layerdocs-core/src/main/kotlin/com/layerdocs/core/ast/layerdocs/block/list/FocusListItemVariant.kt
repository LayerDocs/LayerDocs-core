package com.layerdocs.core.ast.layerdocs.block.list

import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.ListItemVariant
import com.layerdocs.core.ast.base.block.list.ListItemVariantVisitor

/**
 * A list item variant that adds focus to a [ListItem].
 * When at least one item in a list is focused, the other items are rendered so that the focused ones are more visible.
 * This property has an effect only when using a LayerDocs renderer.
 * @param isFocused whether the item is focused.
 */
data class FocusListItemVariant(
    val isFocused: Boolean,
) : ListItemVariant {
    override fun <T> accept(visitor: ListItemVariantVisitor<T>) = visitor.visit(this)
}
