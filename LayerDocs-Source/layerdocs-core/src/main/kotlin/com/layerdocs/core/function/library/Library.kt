package com.layerdocs.core.function.library

import com.layerdocs.core.context.Context
import com.layerdocs.core.function.Function
import com.layerdocs.core.function.value.OutputValue
import com.layerdocs.core.pipeline.PipelineHooks

/**
 * A bundle of functions that can be called from a LayerDocs source.
 * @param name name of the library
 * @param functions functions the library makes available to call
 * @param onLoad optional action to run when the library is loaded in a context. Returns an optional value to be used as the result of loading the library
 * @param hooks optional actions to run after each stage of a pipeline where this library is registered in has been completed
 */
data class Library(
    val name: String,
    val functions: Set<Function<*>>,
    val onLoad: ((Context) -> OutputValue<*>)? = null,
    val hooks: PipelineHooks? = null,
) {
    /**
     * @return a copy of this library with the given pipeline hooks attached
     */
    fun withHooks(hooks: PipelineHooks) = copy(hooks = hooks)
}
