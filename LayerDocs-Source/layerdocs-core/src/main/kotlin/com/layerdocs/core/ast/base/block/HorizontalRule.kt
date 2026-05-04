package com.layerdocs.core.ast.base.block

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A horizontal line (thematic break).
 */
object HorizontalRule : Node {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
