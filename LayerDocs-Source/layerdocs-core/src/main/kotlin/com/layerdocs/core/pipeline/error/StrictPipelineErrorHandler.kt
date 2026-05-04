package com.layerdocs.core.pipeline.error

import com.layerdocs.core.function.Function
import com.layerdocs.core.log.Log

/**
 * Pipeline error handler that rethrows the incoming error and ignores the additional custom action.
 * In a regular pipeline, this will cause the program to exit (see `LayerDocsCli` from the `cli` module).
 */
class StrictPipelineErrorHandler : PipelineErrorHandler {
    override fun <T> handle(
        error: Throwable,
        sourceFunction: Function<*>?,
        action: () -> T,
    ): Nothing {
        val errorCode = (error as? PipelineException)?.code ?: "Unknown - ${error::class.simpleName}"
        Log.error("An error occurred while in strict mode (error code $errorCode)")
        sourceFunction?.let { Log.error("Originated from function: ${it.name}") }
        throw error
    }
}
