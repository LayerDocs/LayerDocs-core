package com.layerdocs.core.flavor.base

import com.layerdocs.core.flavor.LexerFactory
import com.layerdocs.core.flavor.MarkdownFlavor
import com.layerdocs.core.flavor.ParserFactory
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.flavor.TreeIteratorFactory

/**
 * The vanilla [CommonMark](https://spec.commonmark.org) Markdown with several [GFM](https://github.github.com/gfm) features and extensions.
 */
object BaseMarkdownFlavor : MarkdownFlavor {
    override val lexerFactory: LexerFactory = BaseMarkdownLexerFactory
    override val parserFactory: ParserFactory = BaseMarkdownParserFactory()
    override val rendererFactory: RendererFactory = BaseMarkdownRendererFactory
    override val treeIteratorFactory: TreeIteratorFactory = BaseMarkdownTreeIteratorFactory()
}
