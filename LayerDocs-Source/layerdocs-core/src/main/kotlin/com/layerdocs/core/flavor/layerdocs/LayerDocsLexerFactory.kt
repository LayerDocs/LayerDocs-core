package com.layerdocs.core.flavor.layerdocs

import com.layerdocs.core.flavor.InlineLexerVariant
import com.layerdocs.core.flavor.LexerFactory
import com.layerdocs.core.flavor.base.BaseMarkdownLexerFactory
import com.layerdocs.core.lexer.Lexer
import com.layerdocs.core.lexer.patterns.FunctionCallPatterns
import com.layerdocs.core.lexer.patterns.LayerDocsBlockTokenRegexPatterns
import com.layerdocs.core.lexer.patterns.LayerDocsInlineTokenRegexPatterns
import com.layerdocs.core.lexer.regex.StandardRegexLexer
import com.layerdocs.core.lexer.tokens.PlainTextToken

/**
 * [LayerDocsFlavor] lexer factory.
 */
object LayerDocsLexerFactory : LexerFactory {
    private val blockPatterns = LayerDocsBlockTokenRegexPatterns()
    private val inlinePatterns = LayerDocsInlineTokenRegexPatterns()
    private val functionCallPatterns = FunctionCallPatterns()
    private val base = BaseMarkdownLexerFactory

    /**
     * Inserts patterns of LayerDocs's inline extensions into the base inline lexer (produced by [BaseMarkdownLexerFactory]).
     * @return a copy of the base inline lexer also containing LayerDocs's inline extensions.
     */
    private fun StandardRegexLexer.insertInlineExtensions(): Lexer {
        // New inline patterns introduced by this flavor on top of the base patterns.
        val inlineExtensions =
            with(inlinePatterns) {
                listOf(
                    inlineFunctionCall,
                    inlineMath,
                    *textReplacements.toTypedArray(),
                )
            }

        // The last pattern is the critical content one, which should always be last.
        return this.updatePatterns { patterns ->
            patterns.dropLast(1) + inlineExtensions + patterns.last()
        }
    }

    override fun newBlockLexer(source: CharSequence): Lexer =
        with(blockPatterns) {
            StandardRegexLexer(
                source,
                listOf(
                    comment,
                    functionCall,
                    blockQuote,
                    blockCode,
                    footnoteDefinition,
                    linkDefinition,
                    fencesCode,
                    multilineMath,
                    onelineMath,
                    heading,
                    horizontalRule,
                    pageBreak,
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

    override fun newListLexer(source: CharSequence): Lexer = base.newListLexer(source)

    override fun newInlineLexer(
        source: CharSequence,
        variant: InlineLexerVariant,
    ): Lexer = base.newInlineLexer(source, variant).insertInlineExtensions()

    override fun newExpressionLexer(
        source: CharSequence,
        allowBlockFunctionCalls: Boolean,
    ): Lexer =
        with(inlinePatterns) {
            // A function call argument contains textual content (string/number/...)
            // and possibly other nested function calls.
            StandardRegexLexer(
                source,
                if (allowBlockFunctionCalls) {
                    listOf(
                        escape,
                        functionCallPatterns.expressionBlockFunctionCall,
                        inlineFunctionCall,
                    )
                } else {
                    listOf(
                        escape,
                        inlineFunctionCall,
                    )
                },
                fillTokenType = ::PlainTextToken,
            )
        }

    /**
     * Creates a lexer for inline function calls.
     * This lexer is mainly used for function call completion and highlighting in the LSP.
     * @param source the source text to tokenize
     * @return a lexer that recognizes inline function calls
     *         (block arguments are not included, as they are part of block function calls)
     */
    fun newInlineFunctionCallLexer(source: CharSequence): Lexer =
        StandardRegexLexer(
            source,
            listOf(inlinePatterns.inlineFunctionCall),
        )
}
