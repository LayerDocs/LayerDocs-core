package com.layerdocs.core.pipeline.error

import com.layerdocs.core.function.Function
import com.layerdocs.core.log.Log

/**
 * Simple pipeline error handler that logs the error message.
 */
class BasePipelineErrorHandler : PipelineErrorHandler {
    override fun <T> handle(
        error: Throwable,
        sourceFunction: Function<*>?,
        action: () -> T,
    ): T {
        val message = error.message ?: "Unknown error"
        Log.error(message)
        return action()
    }
}
