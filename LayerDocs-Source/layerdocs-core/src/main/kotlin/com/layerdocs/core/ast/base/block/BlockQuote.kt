package com.layerdocs.core.ast.base.block

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.rendering.representable.RenderRepresentable
import com.layerdocs.core.rendering.representable.RenderRepresentableVisitor
import com.layerdocs.core.util.node.group
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A block quote.
 * @param type information type. If `null`, the quote does not have a particular type
 * @param attribution additional author or source of the quote
 * @param content body content of the quote
 */
class BlockQuote(
    val type: Type? = null,
    val attribution: InlineContent? = null,
    val content: List<Node>,
) : NestableNode {
    override val children: List<Node>
        get() = content + attribution.group()

    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)

    /**
     * Type a [BlockQuote] might have.
     */
    enum class Type : RenderRepresentable {
        TIP,
        NOTE,
        WARNING,
        IMPORTANT,
        ;

        override fun <T> accept(visitor: RenderRepresentableVisitor<T>): T = visitor.visit(this)
    }
}
