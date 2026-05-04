package com.layerdocs.core.flavor.base

import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.ParserFactory
import com.layerdocs.core.parser.BlockTokenParser
import com.layerdocs.core.parser.InlineTokenParser

/**
 * [BaseMarkdownFlavor] parser factory.
 */
class BaseMarkdownParserFactory : ParserFactory {
    override fun newBlockParser(context: MutableContext) = BlockTokenParser(context)

    override fun newInlineParser(context: MutableContext) = InlineTokenParser(context)
}
