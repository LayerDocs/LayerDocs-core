package com.layerdocs.core.context.hooks.reference

import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.ast.layerdocs.reference.CrossReference
import com.layerdocs.core.ast.layerdocs.reference.CrossReferenceableNode
import com.layerdocs.core.context.MutableContext

/**
 * A [ReferenceDefinitionResolverHook] that associates a [CrossReferenceableNode] to each [CrossReference] by means of matching IDs.
 */
class CrossReferenceResolverHook(
    context: MutableContext,
) : ReferenceDefinitionResolverHook<CrossReference, CrossReferenceableNode, CrossReferenceableNode>(context) {
    override fun collectReferences(iterator: ObservableAstIterator) = iterator.collectAll<CrossReference>()

    override fun collectDefinitions(iterator: ObservableAstIterator) = iterator.collectAll<CrossReferenceableNode>()

    override fun findDefinitionPair(
        reference: CrossReference,
        definitions: List<CrossReferenceableNode>,
        index: Int,
    ): Pair<CrossReferenceableNode, CrossReferenceableNode>? =
        definitions
            .find { reference.referenceId == it.referenceId }
            ?.let { it to it }
}
