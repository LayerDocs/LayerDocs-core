package com.layerdocs.core.function.error

import com.layerdocs.core.UNRESOLVED_REFERENCE_EXIT_CODE
import com.layerdocs.core.pipeline.error.PipelineException

/**
 * An exception thrown when a function call does not reference any registered function declaration.
 * @param symbol function name
 */
class UnresolvedReferenceException(
    symbol: String,
) : PipelineException("Unresolved reference: $symbol", UNRESOLVED_REFERENCE_EXIT_CODE)
