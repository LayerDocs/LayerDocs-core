package com.layerdocs.lsp.subservices

import com.layerdocs.lsp.TextDocument
import com.layerdocs.lsp.highlight.SemanticTokenData
import com.layerdocs.lsp.highlight.SemanticTokensEncoder
import com.layerdocs.lsp.highlight.SemanticTokensSupplier
import com.layerdocs.lsp.highlight.toSemanticData
import org.eclipse.lsp4j.SemanticTokens
import org.eclipse.lsp4j.SemanticTokensParams

/**
 * Subservice for handling semantic tokens requests.
 * @param tokensSuppliers suppliers of semantic tokens
 */
class SemanticTokensSubservice(
    private val tokensSuppliers: List<SemanticTokensSupplier>,
) : TextDocumentSubservice<SemanticTokensParams, SemanticTokens> {
    override fun process(
        params: SemanticTokensParams,
        document: TextDocument,
    ): SemanticTokens {
        val tokens: List<SemanticTokenData> =
            this.tokensSuppliers
                .flatMap { it.getTokens(params, document) }
                .map { it.toSemanticData(document.text) }

        val encoded = SemanticTokensEncoder.encode(tokens)
        return SemanticTokens(encoded)
    }
}
