package com.layerdocs.lsp.ontype

import com.layerdocs.lsp.TextDocument
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams
import org.eclipse.lsp4j.TextEdit

/**
 * Supplier of text edits for on-type formatting.
 */
interface OnTypeFormattingEditSupplier {
    /**
     * Provides text edits for on-type formatting based on the given parameters and document.
     * @param params the parameters for the on-type formatting request
     * @param document the text document to format
     * @return a list of text edits to apply to the document
     */
    fun getEdits(
        params: DocumentOnTypeFormattingParams,
        document: TextDocument,
    ): List<TextEdit>
}
