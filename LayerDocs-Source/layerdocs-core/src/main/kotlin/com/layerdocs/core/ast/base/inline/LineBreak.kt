package com.layerdocs.core.ast.base.inline

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A hard line break.
 */
object LineBreak : Node {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
