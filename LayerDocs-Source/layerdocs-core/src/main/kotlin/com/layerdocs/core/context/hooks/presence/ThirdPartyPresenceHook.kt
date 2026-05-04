package com.layerdocs.core.context.hooks.presence

import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.presence.markCodePresence
import com.layerdocs.core.ast.attributes.presence.markMathPresence
import com.layerdocs.core.ast.attributes.presence.markMermaidDiagramPresence
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.iterator.AstIteratorHook
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.ast.layerdocs.block.Math
import com.layerdocs.core.ast.layerdocs.block.MermaidDiagram
import com.layerdocs.core.ast.layerdocs.block.SubdocumentGraph
import com.layerdocs.core.ast.layerdocs.inline.MathSpan
import com.layerdocs.core.context.MutableContext

// Hooks that mark the presence of third-party elements in the document,
// in order to conditionally load third-party libraries in the final artifact.

/**
 * Hook that marks the presence of code elements in the [context]'s attributes
 * if at least one [Code] block is present in the document.
 * @see com.layerdocs.core.ast.attributes.presence.CodePresenceProperty
 */
class CodePresenceHook(
    private val context: MutableContext,
) : AstIteratorHook {
    override fun attach(iterator: ObservableAstIterator) {
        iterator.on<Code> { context.attributes.markCodePresence() }
    }
}

/**
 * Hook that marks the presence of math elements in the [context]'s attributes
 * if at least one [Math] or [MathSpan] block is present in the document.
 * @see com.layerdocs.core.ast.attributes.presence.MathPresenceProperty
 */
class MathPresenceHook(
    private val context: MutableContext,
) : AstIteratorHook {
    private val action: (Node) -> Unit
        get() = { context.attributes.markMathPresence() }

    override fun attach(iterator: ObservableAstIterator) {
        iterator
            .on<Math>(action)
            .on<MathSpan>(action)
    }
}

/**
 * Hook that marks the presence of code elements in the [context]'s attributes
 * if at least one [Code] block is present in the document.
 * @see com.layerdocs.core.ast.attributes.presence.MermaidDiagramPresenceProperty
 */
class MermaidDiagramPresenceHook(
    private val context: MutableContext,
) : AstIteratorHook {
    override fun attach(iterator: ObservableAstIterator) {
        iterator.on<MermaidDiagram> { context.attributes.markMermaidDiagramPresence() }
        iterator.on<SubdocumentGraph> { context.attributes.markMermaidDiagramPresence() }
    }
}
