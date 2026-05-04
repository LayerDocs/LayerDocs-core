package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.util.node.group
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A collapsible block, whose content can be hidden or shown by interacting with it.
 * @param title title of the block
 * @param isOpen whether the block is open at the beginning
 * @param content body content of the block
 */
class Collapse(
    val title: InlineContent,
    val isOpen: Boolean,
    val content: List<Node>,
) : NestableNode {
    override val children: List<Node>
        get() = content + title.group()

    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
