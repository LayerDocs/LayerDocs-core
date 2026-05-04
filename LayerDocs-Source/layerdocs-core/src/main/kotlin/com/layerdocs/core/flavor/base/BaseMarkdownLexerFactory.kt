package com.layerdocs.core.flavor.base

import com.layerdocs.core.flavor.InlineLexerVariant
import com.layerdocs.core.flavor.LexerFactory
import com.layerdocs.core.lexer.Lexer
import com.layerdocs.core.lexer.patterns.BaseMarkdownBlockTokenRegexPatterns
import com.layerdocs.core.lexer.patterns.BaseMarkdownInlineTokenRegexPatterns
import com.layerdocs.core.lexer.regex.StandardRegexLexer
import com.layerdocs.core.lexer.tokens.PlainTextToken

/**
 * [BaseMarkdownFlavor] lexer factory.
 */
object BaseMarkdownLexerFactory : LexerFactory {
    private val blockPatterns = BaseMarkdownBlockTokenRegexPatterns()
    private val inlinePatterns = BaseMarkdownInlineTokenRegexPatterns()

    override fun newBlockLexer(source: CharSequence): StandardRegexLexer =
        with(blockPatterns) {
            StandardRegexLexer(
                source,
                listOf(
                    comment,
                    blockQuote,
                    blockCode,
                    footnoteDefinition,
                    linkDefinition,
                    fencesCode,
                    heading,
                    horizontalRule,
                    setextHeading,
                    table,
                    unorderedList,
                    orderedList,
                    newline,
                    paragraph,
                    blockText,
                ),
            )
        }

    override fun newListLexer(source: CharSequence): StandardRegexLexer =
        with(blockPatterns) {
            StandardRegexLexer(
                source,
                listOf(listItem, newline),
            )
        }

    override fun newInlineLexer(
        source: CharSequence,
        variant: InlineLexerVariant,
    ): StandardRegexLexer =
        with(inlinePatterns) {
            val acceptLinks = variant != InlineLexerVariant.LINK_LABEL

            val linkPatterns =
                if (acceptLinks) {
                    arrayOf(
                        diamondAutolink,
                        link,
                        referenceFootnote,
                        referenceLink,
                        urlAutolink,
                    )
                } else {
                    emptyArray()
                }

            StandardRegexLexer(
                source,
                listOf(
                    *linkPatterns,
                    lineBreak,
                    codeSpan,
                    escape,
                    entity,
                    comment,
                    image,
                    referenceImage,
                    strongEmphasisAsterisk,
                    strongEmphasisUnderscore,
                    emphasisAsterisk,
                    emphasisUnderscore,
                    strongAsterisk,
                    strongUnderscore,
                    strikethrough,
                    criticalContent,
                ),
                fillTokenType = ::PlainTextToken,
            )
        }

    // Functions aren't supported by this flavor
    override fun newExpressionLexer(
        source: CharSequence,
        allowBlockFunctionCalls: Boolean,
    ): Lexer = StandardRegexLexer(source, patterns = emptyList())
}
