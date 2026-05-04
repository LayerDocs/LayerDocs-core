package com.layerdocs.core.ast.base.block

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Any unknown node type (should not happen).
 */
object BlankNode : Node {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
