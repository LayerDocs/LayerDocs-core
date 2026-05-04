package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.rendering.representable.RenderRepresentable
import com.layerdocs.core.rendering.representable.RenderRepresentableVisitor
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A block whose content is clipped in a path.
 * @param clip type of the clip path
 */
class Clipped(
    val clip: Clip,
    override val children: List<Node>,
) : NestableNode {
    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)

    /**
     * Possible clip types of a [Clipped] block.
     */
    enum class Clip : RenderRepresentable {
        CIRCLE,
        ;

        override fun <T> accept(visitor: RenderRepresentableVisitor<T>) = visitor.visit(this)
    }
}
