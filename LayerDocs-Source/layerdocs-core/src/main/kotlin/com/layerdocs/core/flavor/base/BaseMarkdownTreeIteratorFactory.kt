package com.layerdocs.core.flavor.base

import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.hooks.LinkUrlResolverHook
import com.layerdocs.core.context.hooks.SubdocumentRegistrationHook
import com.layerdocs.core.context.hooks.presence.CodePresenceHook
import com.layerdocs.core.context.hooks.presence.MathPresenceHook
import com.layerdocs.core.context.hooks.presence.MermaidDiagramPresenceHook
import com.layerdocs.core.context.hooks.reference.FootnoteResolverHook
import com.layerdocs.core.context.hooks.reference.LinkDefinitionResolverHook
import com.layerdocs.core.flavor.TreeIteratorFactory

/**
 * [BaseMarkdownFlavor] tree iterator factory.
 */
class BaseMarkdownTreeIteratorFactory : TreeIteratorFactory {
    override fun default(context: MutableContext): ObservableAstIterator =
        ObservableAstIterator()
            // Resolves reference links to their link definitions.
            .attach(LinkDefinitionResolverHook(context))
            // Registers subdocuments.
            .attach(SubdocumentRegistrationHook(context))
            // Resolves local URLs/paths for links and images loaded from different base paths.
            .attach(LinkUrlResolverHook(context))
            // Resolves footnotes.
            .attach(FootnoteResolverHook(context))
            // Allows loading code libraries (e.g. highlight.js syntax highlighting)
            // if at least one code block is present.
            .attach(CodePresenceHook(context))
            // Allows loading Mermaid libraries
            // if at least one diagram is present.
            .attach(MermaidDiagramPresenceHook(context))
            // Allows loading math libraries (e.g. KaTeX)
            // if at least one math block is present.
            .attach(MathPresenceHook(context))
}
