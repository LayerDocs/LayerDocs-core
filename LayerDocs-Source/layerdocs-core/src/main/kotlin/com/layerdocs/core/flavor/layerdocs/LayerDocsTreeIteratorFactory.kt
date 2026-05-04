package com.layerdocs.core.flavor.layerdocs

import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.hooks.MediaStorerHook
import com.layerdocs.core.context.hooks.TableOfContentsGeneratorHook
import com.layerdocs.core.context.hooks.location.LocationAwareLabelStorerHook
import com.layerdocs.core.context.hooks.location.LocationAwarenessHook
import com.layerdocs.core.context.hooks.location.NumberedEvaluatorHook
import com.layerdocs.core.context.hooks.reference.BibliographyCitationResolverHook
import com.layerdocs.core.context.hooks.reference.CrossReferenceResolverHook
import com.layerdocs.core.flavor.TreeIteratorFactory
import com.layerdocs.core.flavor.base.BaseMarkdownTreeIteratorFactory

/**
 * [LayerDocsFlavor] tree iterator factory.
 */
class LayerDocsTreeIteratorFactory : TreeIteratorFactory {
    override fun default(context: MutableContext): ObservableAstIterator =
        BaseMarkdownTreeIteratorFactory()
            .default(context)
            .attach(LocationAwarenessHook(context))
            .attach(LocationAwareLabelStorerHook(context))
            .attach(NumberedEvaluatorHook(context))
            .attach(TableOfContentsGeneratorHook(context))
            .attach(CrossReferenceResolverHook(context))
            .attach(BibliographyCitationResolverHook(context))
            .apply {
                if (context.attachedPipeline?.options?.enableMediaStorage == true) {
                    attach(MediaStorerHook(context))
                }
            }
}
