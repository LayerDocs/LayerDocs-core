package com.layerdocs.core.ast.layerdocs.block.list

import com.layerdocs.core.ast.attributes.location.LocationTrackableNode
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.ListItemVariant
import com.layerdocs.core.ast.base.block.list.ListItemVariantVisitor
import com.layerdocs.core.document.numbering.DocumentNumbering
import com.layerdocs.core.document.numbering.NumberingFormat

/**
 * Variant of a [ListItem] that displays the location of a target node,
 * usually (rendering-dependent) by replacing the item marker with [target]'s position,
 * formatted according to the global numbering format.
 * This is used, for example, in table of contents.
 * @param target node to display the location of
 * @param format kind of numbering format to use to format the location
 */
data class LocationTargetListItemVariant(
    val target: LocationTrackableNode,
    val format: (DocumentNumbering) -> NumberingFormat?,
) : ListItemVariant {
    override fun <T> accept(visitor: ListItemVariantVisitor<T>): T = visitor.visit(this)
}
