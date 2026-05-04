package com.layerdocs.core.function.error

import com.layerdocs.core.NO_SUCH_ELEMENT_EXIT_CODE
import com.layerdocs.core.function.layerdocsName
import com.layerdocs.core.pipeline.error.PipelineException

/**
 * Exception thrown when an element (e.g. an enum value from a LayerDocs function argument)
 * does not exist among elements of a look-up table.
 */
class NoSuchElementException(
    element: Any,
    values: Iterable<*>,
) : PipelineException("No such element '$element' among values $values", NO_SUCH_ELEMENT_EXIT_CODE) {
    constructor(element: Any, values: Array<Enum<*>>) : this(element, values.map { it.layerdocsName })
}
