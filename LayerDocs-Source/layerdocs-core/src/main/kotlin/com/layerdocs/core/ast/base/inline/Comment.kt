package com.layerdocs.core.ast.base.inline

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A comment whose content is ignored.
 */
object Comment : Node {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
