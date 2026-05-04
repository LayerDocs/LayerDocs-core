package com.layerdocs.core.flavor.layerdocs

import com.layerdocs.core.flavor.LexerFactory
import com.layerdocs.core.flavor.MarkdownFlavor
import com.layerdocs.core.flavor.ParserFactory
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.flavor.TreeIteratorFactory

/**
 * [com.layerdocs.core.flavor.base.BaseMarkdownFlavor] extension with, in addition:
 * - Functions
 * - Math blocks
 * - Code span additional content
 * - Image labels
 * - Table of contents
 *
 * And more.
 */
object LayerDocsFlavor : MarkdownFlavor {
    override val lexerFactory: LexerFactory = LayerDocsLexerFactory
    override val parserFactory: ParserFactory = LayerDocsParserFactory()
    override val rendererFactory: RendererFactory = LayerDocsRendererFactory()
    override val treeIteratorFactory: TreeIteratorFactory = LayerDocsTreeIteratorFactory()
}
