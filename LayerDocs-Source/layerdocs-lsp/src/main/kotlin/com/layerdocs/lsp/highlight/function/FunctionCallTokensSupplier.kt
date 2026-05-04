package com.layerdocs.lsp.highlight.function

import com.layerdocs.lsp.TextDocument
import com.layerdocs.lsp.cache.functionCalls
import com.layerdocs.lsp.highlight.SemanticTokensSupplier
import com.layerdocs.lsp.highlight.SimpleTokenData
import com.layerdocs.lsp.highlight.TokenType
import com.layerdocs.lsp.tokenizer.FunctionCallToken
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.BEGIN
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.BODY_ARGUMENT
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.CHAINING_SEPARATOR
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.FUNCTION_NAME
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.INLINE_ARGUMENT_BEGIN
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.INLINE_ARGUMENT_END
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.INLINE_ARGUMENT_VALUE
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.LINE_CONTINUATION
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.NAMED_PARAMETER_DELIMITER
import com.layerdocs.lsp.tokenizer.FunctionCallToken.Type.PARAMETER_NAME
import org.eclipse.lsp4j.SemanticTokensParams

/**
 * Supplier for semantic tokens that highlight function calls.
 */
class FunctionCallTokensSupplier : SemanticTokensSupplier {
    override fun getTokens(
        params: SemanticTokensParams,
        document: TextDocument,
    ): Iterable<SimpleTokenData> =
        document.functionCalls
            .asSequence()
            .flatMap { it.tokens }
            .map { it.toSimpleTokenData() }
            .filterNotNull()
            .toList()

    /**
     * Converts a [FunctionCallToken] to a [SimpleTokenData] suitable for semantic highlighting,
     * or returns `null` if the token type does not correspond to a highlightable token.
     */
    private fun FunctionCallToken.toSimpleTokenData(): SimpleTokenData? {
        val type: TokenType? =
            when (type) {
                BEGIN, FUNCTION_NAME -> {
                    TokenType.FUNCTION_CALL_IDENTIFIER
                }

                CHAINING_SEPARATOR -> {
                    TokenType.FUNCTION_CALL_CHAINING_SEPARATOR
                }

                PARAMETER_NAME, NAMED_PARAMETER_DELIMITER -> {
                    TokenType.FUNCTION_CALL_NAMED_PARAMETER
                }

                INLINE_ARGUMENT_BEGIN, INLINE_ARGUMENT_END -> {
                    TokenType.FUNCTION_CALL_INLINE_ARGUMENT_DELIMITER
                }

                INLINE_ARGUMENT_VALUE -> {
                    ValueQualifier.getTokenType(lexeme.trim())
                }

                LINE_CONTINUATION -> {
                    TokenType.FUNCTION_CALL_INLINE_ARGUMENT_DELIMITER
                }

                BODY_ARGUMENT -> {
                    null
                }
            }
        return SimpleTokenData(
            type = type ?: return null,
            range = range,
        )
    }
}
