package com.layerdocs.lsp.pattern

import com.layerdocs.core.lexer.patterns.FUNCTION_CALL_PATTERN_BEFORE
import com.layerdocs.core.lexer.regex.RegexBuilder
import com.layerdocs.core.parser.walker.funcall.FunctionCallGrammar
import com.layerdocs.lsp.pattern.LayerDocsPatterns.FunctionCall.BEGIN

/**
 * Patterns used by the LayerDocs lexer.
 */
object LayerDocsPatterns {
    /**
     * Patterns related to function calls.
     */
    object FunctionCall {
        /**
         * The character that prefixes a function call.
         */
        const val BEGIN: String = FunctionCallGrammar.BEGIN.toString()

        /**
         * The pattern for an identifier (function name or argument name).
         */
        val IDENTIFIER: Regex = FunctionCallGrammar.IDENTIFIER_PATTERN.toRegex()

        /**
         * The pattern that chains function calls together.
         */
        const val CHAIN_SEPARATOR: String = FunctionCallGrammar.CHAIN_SEPARATOR

        /**
         * The character that begins an inline argument.
         */
        const val ARGUMENT_BEGIN = FunctionCallGrammar.ARGUMENT_BEGIN.toString()

        /**
         * The character that ends an inline argument.
         */
        const val ARGUMENT_END = FunctionCallGrammar.ARGUMENT_END.toString()

        /**
         * The character that delimits a named argument.
         */
        const val NAMED_ARGUMENT_DELIMITER = FunctionCallGrammar.NAMED_ARGUMENT_DELIMITER

        /**
         * Default/suggested indentation for the body argument of a function call.
         */
        const val CONVENTIONAL_BODY_INDENT = "    "

        /**
         * The pattern that matches an optional identifier in a function call, preceded by [BEGIN] (unmatched).
         */
        val identifierInCall: Regex =
            RegexBuilder("(?<=(before)(begin))(identifier)?")
                .withReference("before", FUNCTION_CALL_PATTERN_BEFORE)
                .withReference("begin", Regex.escape(BEGIN))
                .withReference("chain", Regex.escape(CHAIN_SEPARATOR))
                .withReference("identifier", IDENTIFIER.pattern)
                .buildRegex()
    }
}
