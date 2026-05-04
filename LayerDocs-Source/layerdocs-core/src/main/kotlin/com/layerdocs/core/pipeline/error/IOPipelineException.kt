package com.layerdocs.core.pipeline.error

import com.layerdocs.core.IO_ERROR_EXIT_CODE

/**
 * Errors thrown when an I/O error occurs in the pipeline.
 */
class IOPipelineException(
    message: String,
) : PipelineException(message, IO_ERROR_EXIT_CODE)
