package com.layerdocs.core.ast.base.block

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.base.TextNode
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A general paragraph.
 * @param text text content
 */
class Paragraph(
    override val text: InlineContent,
) : TextNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
