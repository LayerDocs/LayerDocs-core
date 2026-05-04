package com.layerdocs.core.function.library.loader

import com.layerdocs.core.function.library.Library
import com.layerdocs.core.function.reflect.KFunctionAdapter
import com.layerdocs.core.function.value.OutputValue
import kotlin.reflect.KFunction

/**
 * A LayerDocs function that can be exported via a [FunctionLibraryLoader].
 */
typealias ExportableFunction = KFunction<OutputValue<*>>

/**
 * Creates a library from a single Kotlin function.
 * @see KFunctionAdapter
 */
class FunctionLibraryLoader : LibraryLoader<ExportableFunction> {
    override fun load(source: ExportableFunction) = Library(source.name, setOf(KFunctionAdapter(source)))
}
