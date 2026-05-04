package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A node that, when rendered in a `Slides` environment in speaker view,
 * contains speaker notes for the current slide.
 */
class SlidesSpeakerNote(
    override val children: List<Node>,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
