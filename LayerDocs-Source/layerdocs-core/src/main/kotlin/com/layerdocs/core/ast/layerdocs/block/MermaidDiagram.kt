package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A Mermaid diagram.
 * @param code Mermaid code of the diagram
 */
class MermaidDiagram(
    val code: String,
) : Node {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
