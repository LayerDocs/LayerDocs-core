package com.layerdocs.lsp.cache

import com.layerdocs.layerdoc.reader.DocsFunction
import com.layerdocs.layerdoc.reader.DocsWalker
import org.eclipse.lsp4j.MarkupContent

/**
 * Cached information extracted from the LayerDocs documentation about a function.
 * @param data the processed function data
 * @param rawData the raw data from the documentation walker
 * @param documentationAsMarkup the documentation content as markup, if available, supported by the LSP
 */
data class DocumentedFunction(
    val data: DocsFunction,
    val rawData: DocsWalker.Result<*>,
    val documentationAsMarkup: MarkupContent?,
) {
    /**
     * The name of the function.
     */
    val name: String
        get() = data.name
}
