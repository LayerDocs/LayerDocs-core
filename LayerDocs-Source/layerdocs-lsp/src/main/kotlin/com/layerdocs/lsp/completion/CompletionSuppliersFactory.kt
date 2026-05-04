package com.layerdocs.lsp.completion

import com.layerdocs.lsp.LayerDocsLanguageServer
import com.layerdocs.lsp.completion.function.name.FunctionNameCompletionSupplier
import com.layerdocs.lsp.completion.function.parameter.FunctionParameterAllowedValuesCompletionSupplier
import com.layerdocs.lsp.completion.function.parameter.FunctionParameterNameCompletionSupplier
import java.io.File

/**
 * Factory for creating a list of [CompletionSupplier]s.
 */
object CompletionSuppliersFactory {
    /**
     * The default completion suppliers cover completions for:
     * - Function name ([FunctionNameCompletionSupplier]), both regular and chained
     * - Function parameter name ([FunctionParameterNameCompletionSupplier])
     * - Function parameter values ([FunctionParameterAllowedValuesCompletionSupplier])
     *
     * @param server the LayerDocs language server instance
     * @return the default list of [CompletionSupplier] instances
     */
    fun default(server: LayerDocsLanguageServer): List<CompletionSupplier> = this.functions(docsDirectory = server.docsDirectoryOrThrow())

    /**
     * @param docsDirectory the directory containing the documentation files
     * @return the [CompletionSupplier]s that handle function call completions
     */
    internal fun functions(docsDirectory: File): List<CompletionSupplier> =
        listOf(
            FunctionNameCompletionSupplier(docsDirectory),
            FunctionParameterAllowedValuesCompletionSupplier(docsDirectory),
            FunctionParameterNameCompletionSupplier(docsDirectory),
        )
}
