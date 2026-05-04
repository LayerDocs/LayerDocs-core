package com.layerdocs.core.ast.base.inline

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.base.TextNode
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Weakly emphasized content.
 * @param text content
 */
class Emphasis(
    override val text: InlineContent,
) : TextNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}

/**
 * Strongly emphasized content.
 * @param text content
 */
class Strong(
    override val text: InlineContent,
) : TextNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}

/**
 * Heavily emphasized content.
 * @param text content
 */
class StrongEmphasis(
    override val text: InlineContent,
) : TextNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}

/**
 * Strikethrough content.
 * @param text content
 */
class Strikethrough(
    override val text: InlineContent,
) : TextNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}
