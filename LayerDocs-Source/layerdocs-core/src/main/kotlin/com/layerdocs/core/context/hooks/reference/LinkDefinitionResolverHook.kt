package com.layerdocs.core.context.hooks.reference

import com.layerdocs.core.ast.attributes.link.getResolvedUrl
import com.layerdocs.core.ast.attributes.reference.ReferenceNode
import com.layerdocs.core.ast.base.LinkNode
import com.layerdocs.core.ast.base.block.LinkDefinition
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.ReferenceImage
import com.layerdocs.core.ast.base.inline.ReferenceLink
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.util.node.toPlainText

/**
 * Hook that associates a [LinkDefinition] to each [ReferenceLink],
 * producing a resolved [Link] node that can be retrieved via
 * [com.layerdocs.core.ast.attributes.reference.getDefinition].
 *
 * When a match is found, [ReferenceLink.onResolve] callbacks are triggered,
 * which are used by [com.layerdocs.core.context.hooks.MediaStorerHook]
 * to register media for reference images.
 */
class LinkDefinitionResolverHook(
    context: MutableContext,
) : ReferenceDefinitionResolverHook<ReferenceLink, LinkDefinition, LinkNode>(context) {
    override fun collectReferences(iterator: ObservableAstIterator): List<ReferenceNode<ReferenceLink, LinkNode>> {
        val references = mutableListOf<ReferenceNode<ReferenceLink, LinkNode>>()
        iterator.on<ReferenceLink> { references += it }
        iterator.on<ReferenceImage> { references += it.link }
        return references
    }

    override fun collectDefinitions(iterator: ObservableAstIterator) = iterator.collectAll<LinkDefinition>()

    override fun findDefinitionPair(
        reference: ReferenceLink,
        definitions: List<LinkDefinition>,
        index: Int,
    ): Pair<LinkDefinition, LinkNode>? =
        definitions
            .find { it.label.toPlainText() == reference.referenceLabel.toPlainText() }
            ?.let { definition ->
                val link = Link(reference.label, definition.getResolvedUrl(context), definition.title, definition.fileSystem)
                reference.onResolve.forEach { action -> action(link) }
                definition to link
            }
}
