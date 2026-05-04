package com.layerdocs.core.ast.base.inline

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.reference.ReferenceNode
import com.layerdocs.core.ast.base.block.FootnoteDefinition
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A reference to a [com.layerdocs.core.ast.base.block.FootnoteDefinition].
 * @param label reference label that should match that of the footnote definition
 * @param fallback supplier of the node to show instead of [label] in case the reference is invalid
 */
class ReferenceFootnote(
    val label: String,
    val fallback: () -> Node,
) : ReferenceNode<ReferenceFootnote, FootnoteDefinition> {
    override val reference: ReferenceFootnote = this

    override fun <T> accept(visitor: NodeVisitor<T>) = visitor.visit(this)
}

/**
 * An all-in-one [ReferenceFootnote] that includes its [FootnoteDefinition].
 * @param label the new label of the definition and reference
 * @param definition the content of the footnote definition
 */
class ReferenceDefinitionFootnote(
    val label: String,
    val definition: InlineContent,
) : NestableNode {
    override val children =
        listOf(
            ReferenceFootnote(
                label,
                fallback = { throw IllegalStateException("Reference + definition footnote should not need a fallback") },
            ),
            FootnoteDefinition(
                label,
                definition,
            ),
        )

    override fun <T> accept(visitor: NodeVisitor<T>): T = AstRoot(children).accept(visitor)
}
