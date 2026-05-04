package com.layerdocs.lsp.documentation

import com.layerdocs.core.parser.walker.funcall.lastChainedCall
import com.layerdocs.lsp.cache.CacheableFunctionCatalogue
import com.layerdocs.lsp.cache.DocumentedFunction
import com.layerdocs.lsp.tokenizer.FunctionCall
import java.io.File

/**
 * Retrieves the documentation for a function in the specified documentation directory.
 * @param docsDirectory the directory containing the documentation files
 * @param name name of the function to look up
 * @return the [DocumentedFunction] if found
 */
fun getDocumentation(
    docsDirectory: File,
    name: String,
): DocumentedFunction? =
    CacheableFunctionCatalogue
        .getCatalogue(docsDirectory)
        .find { it.name == name }

/**
 * Retrieves the documentation for a function call in the specified documentation directory.
 * @param docsDirectory the directory containing the documentation files
 * @return the [DocumentedFunction] if found
 */
fun FunctionCall.getDocumentation(docsDirectory: File): DocumentedFunction? =
    getDocumentation(docsDirectory, this.parserResult.value.lastChainedCall.name)
