package com.layerdocs.lsp.hover

import com.layerdocs.lsp.LayerDocsLanguageServer
import com.layerdocs.lsp.hover.function.FunctionDocumentationHoverSupplier

/**
 * Factory for creating a list of [HoverSupplier]s.
 */
object HoverSuppliersFactory {
    /**
     * @param server the LayerDocs language server instance
     * @return the default list of [HoverSupplier] instances
     */
    fun default(server: LayerDocsLanguageServer) =
        listOf(
            FunctionDocumentationHoverSupplier(
                docsDirectory = server.docsDirectoryOrThrow(),
            ),
        )
}
