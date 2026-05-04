package com.layerdocs.lsp.completion.function.name

import com.layerdocs.lsp.TextDocument
import com.layerdocs.lsp.cache.CacheableFunctionCatalogue
import com.layerdocs.lsp.completion.CompletionSupplier
import com.layerdocs.lsp.completion.toCompletionItem
import com.layerdocs.lsp.pattern.LayerDocsPatterns
import com.layerdocs.lsp.util.getLineUntilPosition
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionParams
import java.io.File

/**
 * Provides completion items for regular (non-chained) function names by scanning documentation files.
 *
 * Let `|` be the cursor position in the text, this supplier provides completions for:
 * - `.|`
 * - `.func|`
 *
 * This supplier is proxied by [FunctionNameCompletionSupplier].
 * @param docsDirectory the directory containing the documentation files to extract function data from
 */
class RegularFunctionNameCompletionSupplier(
    private val docsDirectory: File,
) : CompletionSupplier {
    // Pattern to match a function call at cursor position.
    private val callPattern = Regex("${LayerDocsPatterns.FunctionCall.identifierInCall}$")

    override fun getCompletionItems(
        params: CompletionParams,
        document: TextDocument,
    ): List<CompletionItem> {
        val text = document.text
        val line = params.position.getLineUntilPosition(text) ?: return emptyList()

        // The name of the function call at the cursor position to complete.
        val snippet: String = callPattern.find(line)?.value ?: return emptyList()

        return CacheableFunctionCatalogue
            .searchAll(this.docsDirectory, snippet)
            .map { it.toCompletionItem(chained = false) }
            .toList()
    }
}
