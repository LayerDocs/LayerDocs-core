package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A forced page break.
 */
class PageBreak : Node {
    override fun toString() = "PageBreak"

    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
