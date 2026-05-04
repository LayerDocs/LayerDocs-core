package com.layerdocs.lsp.diagnostics

import com.layerdocs.lsp.LayerDocsLanguageServer
import com.layerdocs.lsp.diagnostics.function.FunctionDuplicateParameterNameDiagnosticsSupplier
import com.layerdocs.lsp.diagnostics.function.FunctionParameterValueDiagnosticsSupplier
import com.layerdocs.lsp.diagnostics.function.FunctionUnresolvedParameterNameDiagnosticsSupplier

/**
 * Factory for creating a list of [DiagnosticsSupplier]s.
 */
object DiagnosticsSuppliersFactory {
    /**
     * @param server the LayerDocs language server instance
     * @return the default list of [DiagnosticsSuppliersFactory] instances
     */
    fun default(server: LayerDocsLanguageServer): List<DiagnosticsSupplier> {
        val docsDirectory = server.docsDirectoryOrThrow()
        return listOf(
            FunctionParameterValueDiagnosticsSupplier(docsDirectory),
            FunctionUnresolvedParameterNameDiagnosticsSupplier(docsDirectory),
            FunctionDuplicateParameterNameDiagnosticsSupplier(),
        )
    }
}
