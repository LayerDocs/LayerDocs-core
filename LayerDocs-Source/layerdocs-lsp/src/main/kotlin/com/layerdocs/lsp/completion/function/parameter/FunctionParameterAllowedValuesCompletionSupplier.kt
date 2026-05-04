package com.layerdocs.lsp.completion.function.parameter

import com.layerdocs.lsp.cache.DocumentedFunction
import com.layerdocs.lsp.completion.function.AbstractFunctionCompletionSupplier
import com.layerdocs.lsp.completion.toCompletionItem
import com.layerdocs.lsp.tokenizer.FunctionCall
import com.layerdocs.lsp.tokenizer.FunctionCallToken
import com.layerdocs.lsp.tokenizer.getTokenAtSourceIndex
import com.layerdocs.lsp.util.getParameterAtSourceIndex
import com.layerdocs.layerdoc.reader.DocsParameter
import org.eclipse.lsp4j.CompletionItem
import java.io.File

/**
 * Provides completion items for fixed allowed values for a function parameter, if applicable.
 * For example, let `|` be the cursor position in the text,
 * `.row alignment:{|` will provide allowed values for the `alignment` parameter of the `row` function.
 *
 * This works for both named and positional arguments.
 */
class FunctionParameterAllowedValuesCompletionSupplier(
    docsDirectory: File,
) : AbstractFunctionCompletionSupplier(docsDirectory) {
    override fun getCompletionItems(
        call: FunctionCall,
        function: DocumentedFunction?,
        cursorIndex: Int,
        originalCursorIndex: Int,
    ): List<CompletionItem> {
        if (function == null) return emptyList()

        // If a value is partially present, it can be completed.
        // If no value is present, all allowed values are returned.
        val value: String =
            call
                .getTokenAtSourceIndex(cursorIndex)
                ?.takeIf { it.type == FunctionCallToken.Type.INLINE_ARGUMENT_VALUE }
                ?.lexeme
                ?.trim()
                ?: ""

        // The parameter data looked up from documentation.
        val parameter: DocsParameter = call.getParameterAtSourceIndex(function.data, cursorIndex) ?: return emptyList()

        return parameter.allowedValues
            ?.filter { it.startsWith(value) }
            ?.map { it.toCompletionItem() }
            ?: emptyList()
    }
}
