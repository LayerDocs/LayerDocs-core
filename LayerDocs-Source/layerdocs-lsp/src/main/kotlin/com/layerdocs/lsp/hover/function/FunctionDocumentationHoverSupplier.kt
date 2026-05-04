package com.layerdocs.lsp.hover.function

import com.layerdocs.lsp.TextDocument
import com.layerdocs.lsp.cache.DocumentedFunction
import com.layerdocs.lsp.cache.functionCalls
import com.layerdocs.lsp.documentation.getDocumentation
import com.layerdocs.lsp.hover.HoverSupplier
import com.layerdocs.lsp.tokenizer.FunctionCall
import com.layerdocs.lsp.tokenizer.FunctionCallToken
import com.layerdocs.lsp.tokenizer.getAtSourceIndex
import com.layerdocs.lsp.tokenizer.getTokenAtSourceIndex
import com.layerdocs.lsp.util.toOffset
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.HoverParams
import java.io.File

/**
 * Provider of documentation on hover for function calls.
 * @property docsDirectory the directory containing the documentation files
 */
class FunctionDocumentationHoverSupplier(
    private val docsDirectory: File,
) : HoverSupplier {
    override fun getHover(
        params: HoverParams,
        document: TextDocument,
    ): Hover? {
        val text = document.text

        // Gets the function call at the specified hover position.
        val index = params.position.toOffset(text)
        val call: FunctionCall =
            document.functionCalls
                .getAtSourceIndex(index)
                ?: return null

        // If the hover position is over a function name in the chain, shows documentation for that specific function.
        // Otherwise, shows documentation for the last function in the chain.
        val nameToken: FunctionCallToken? =
            call
                .getTokenAtSourceIndex(index)
                ?.takeIf { it.type == FunctionCallToken.Type.FUNCTION_NAME }

        // Returns the documentation to display in the hover.
        val function: DocumentedFunction =
            getDocumentation(docsDirectory, nameToken?.lexeme ?: call.lastChainedName)
                ?: return null

        return Hover(function.documentationAsMarkup)
    }
}
