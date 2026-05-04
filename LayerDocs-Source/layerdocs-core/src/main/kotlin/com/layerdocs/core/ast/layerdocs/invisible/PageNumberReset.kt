package com.layerdocs.core.ast.layerdocs.invisible

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Marker node used to reset the logical page number during rendering.
 * @param startFrom the page number to start from after the reset
 */
class PageNumberReset(
    val startFrom: Int,
) : Node {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
