package com.layerdocs.core.lexer

/**
 * A scanner that transforms raw string data into a list of token.
 * For instance, the Markdown code `Hello _LayerDocs_` is tokenized by its implementation into `Hello `, `_LayerDocs_`.
 */
interface Lexer {
    /**
     * The content to be tokenized.
     */
    val source: CharSequence

    /**
     * Disassembles some raw string into smaller tokens.
     * @return a lazy sequence of tokens, produced on demand
     */
    fun tokenize(): Sequence<Token>
}
