package com.layerdocs.core.ast.base

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node

/**
 * A node that may contain inline content as its children.
 */
interface TextNode : NestableNode {
    /**
     * The text of the node as processed inline content.
     */
    val text: InlineContent

    override val children: List<Node>
        get() = text
}
