package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Transposes content to landscape orientation by rotating it 90 degrees counter-clockwise
 * with respect to the page size.
 */
class Landscape(
    override val children: List<Node>,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
