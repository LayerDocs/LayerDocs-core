package com.layerdocs.core.context.hooks

import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.iterator.AstIteratorHook
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.toc.TableOfContents

/**
 * Hook that allows the generation of a [TableOfContents] by iterating through [Heading]s.
 * The [TableOfContents] is stored in the [context]'s [MutableContext.attributes] at the end of the traversal.
 */
class TableOfContentsGeneratorHook(
    private val context: MutableContext,
) : AstIteratorHook {
    override fun attach(iterator: ObservableAstIterator) {
        val headings = iterator.collect<Heading> { !it.excludeFromTableOfContents }

        // Generation.
        iterator.onFinished {
            context.attributes.tableOfContents = TableOfContents.generate(headings.asSequence())
        }
    }
}
