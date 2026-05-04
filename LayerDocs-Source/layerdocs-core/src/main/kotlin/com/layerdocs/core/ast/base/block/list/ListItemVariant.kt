package com.layerdocs.core.ast.base.block.list

import com.layerdocs.core.ast.layerdocs.block.list.FocusListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.LocationTargetListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.TableOfContentsItemVariant

/**
 * A variant of a [ListItem] that brings additional functionalities to it.
 */
interface ListItemVariant {
    /**
     * Accepts a [ListItemVariantVisitor].
     * @param visitor visitor to accept
     * @return result of the visit operation
     */
    fun <T> accept(visitor: ListItemVariantVisitor<T>): T
}

/**
 * Visitor of [ListItemVariant]s.
 * @param T return type of the visit operations
 */
interface ListItemVariantVisitor<T> {
    fun visit(variant: TaskListItemVariant): T

    fun visit(variant: FocusListItemVariant): T

    fun visit(variant: LocationTargetListItemVariant): T

    fun visit(variant: TableOfContentsItemVariant): T
}
