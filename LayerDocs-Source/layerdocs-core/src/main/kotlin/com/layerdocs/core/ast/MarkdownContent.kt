package com.layerdocs.core.ast

import com.layerdocs.core.visitor.node.NodeVisitor

// Utility nodes that are used as input in LayerDocs functions to expect Markdown data as an argument.

/**
 * A generic group of block nodes used as input for LayerDocs functions.
 * @see com.layerdocs.core.function.value.factory.ValueFactory.blockMarkdown
 */
class MarkdownContent(
    override val children: List<Node>,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(AstRoot(children))
}

/**
 * A generic group of inline nodes used as input for LayerDocs functions.
 * @see com.layerdocs.core.function.value.factory.ValueFactory.inlineMarkdown
 */
class InlineMarkdownContent(
    override val children: InlineContent,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(AstRoot(children))
}
