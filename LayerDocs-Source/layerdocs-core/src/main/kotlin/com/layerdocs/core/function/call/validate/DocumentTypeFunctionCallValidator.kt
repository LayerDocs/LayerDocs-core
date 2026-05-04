package com.layerdocs.core.function.call.validate

import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.error.InvalidFunctionCallException
import com.layerdocs.core.function.layerdocsName
import com.layerdocs.core.function.value.OutputValue

/**
 * Validator of a function call that checks if the document the function call lies in is of a certain type.
 * If not, an [InvalidFunctionCallException] is thrown.
 * @param T output type of the function
 * @param allowedTypes allowed document types
 */
class DocumentTypeFunctionCallValidator<T : OutputValue<*>>(
    private val allowedTypes: Iterable<DocumentType>,
) : FunctionCallValidator<T> {
    override fun validate(call: FunctionCall<T>) {
        val type = call.context?.documentInfo?.type ?: return
        if (type in allowedTypes) {
            return
        }

        throw InvalidFunctionCallException(
            call,
            reason =
                "the function was called in a ${type.layerdocsName} document, " +
                    "while it is allowed only in ${allowedTypes.joinToString { it.layerdocsName }}",
            includeArguments = false,
        )
    }
}
