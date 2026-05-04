package com.layerdocs.core.ast

import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * The root of a node tree.
 */
class AstRoot(
    override val children: List<Node>,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}

typealias Document = AstRoot
