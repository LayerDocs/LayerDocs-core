package com.layerdocs.core.ast.layerdocs.invisible

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.document.layout.page.PageMarginPosition
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A non-visible node that triggers a property in paged documents that allows displaying content on each page.
 * @param children content to be displayed on each page
 * @param position position of the content within the page
 */
class PageMarginContentInitializer(
    override val children: List<Node>,
    val position: PageMarginPosition,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
