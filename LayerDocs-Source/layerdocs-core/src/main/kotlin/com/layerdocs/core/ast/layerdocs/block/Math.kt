package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.attributes.location.LocationTrackableNode
import com.layerdocs.core.ast.layerdocs.reference.CrossReferenceableNode
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A math (TeX) block.
 *
 * A math block can be cross-referenced and can be numbered, as long as it has a [referenceId].
 * @param expression expression content
 * @param referenceId optional reference id for cross-referencing via a [com.layerdocs.core.ast.layerdocs.reference.CrossReference]
 */
class Math(
    val expression: String,
    override val referenceId: String? = null,
) : LocationTrackableNode,
    CrossReferenceableNode {
    /**
     * A math block is numbered if it has a [referenceId].
     */
    override val canTrackLocation: Boolean
        get() = referenceId != null

    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
