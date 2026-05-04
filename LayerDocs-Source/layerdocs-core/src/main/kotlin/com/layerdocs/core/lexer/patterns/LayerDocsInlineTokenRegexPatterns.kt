package com.layerdocs.core.lexer.patterns

import com.layerdocs.core.lexer.regex.RegexBuilder
import com.layerdocs.core.lexer.regex.pattern.TokenRegexPattern
import com.layerdocs.core.lexer.tokens.InlineMathToken

/**
 * Regex patterns for [com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor].
 */
class LayerDocsInlineTokenRegexPatterns : BaseMarkdownInlineTokenRegexPatterns() {
    /**
     * Function name prefixed by '.', followed by a sequence of arguments wrapped in curly braces.
     */
    val inlineFunctionCall by lazy {
        FunctionCallPatterns().inlineFunctionCall
    }

    /**
     * Fenced content within spaced dollar signs on the same line.
     * @see InlineMathToken
     */
    val inlineMath by lazy {
        TokenRegexPattern(
            name = "InlineMath",
            wrap = ::InlineMathToken,
            regex =
                RegexBuilder("(?<=^|\\s|\\W)math(?=$|\\s|\\W)")
                    .withReference("math", PatternHelpers.ONELINE_MATH)
                    .build(),
        )
    }

    /**
     * Patterns for sequences of characters that correspond to text symbols.
     */
    val textReplacements: List<TokenRegexPattern> = TextSymbolReplacement.entries.map { it.toTokenPattern() }
}
