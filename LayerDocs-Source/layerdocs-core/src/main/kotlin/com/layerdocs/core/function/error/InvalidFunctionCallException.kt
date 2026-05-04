package com.layerdocs.core.function.error

import com.layerdocs.core.BAD_FUNCTION_CALL_EXIT_CODE
import com.layerdocs.core.ast.dsl.buildInline
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.call.asString
import com.layerdocs.core.function.signatureAsString

private const val TEXT_AUTOCOLLAPSE_MAX_LENGTH = 40

/**
 * An exception thrown if a [FunctionCall] could not be executed.
 * @param call the invalid call
 * @param reason optional additional reason the call failed for
 * @param includeArguments whether to include supplied function call arguments in the error message
 */
open class InvalidFunctionCallException(
    val call: FunctionCall<*>,
    reason: String? = null,
    includeArguments: Boolean = true,
) : FunctionException(
        richMessage =
            buildInline {
                text("Cannot call function ${call.function.name}")
                // If the signature is too long, it is collapsed by default and can be expanded by the user.
                autoCollapse(
                    text = call.function.signatureAsString(includeName = false),
                    maxLength = TEXT_AUTOCOLLAPSE_MAX_LENGTH,
                )

                if (includeArguments) {
                    text(" with arguments ")
                    // The same applies to arguments.
                    autoCollapse(
                        text = call.arguments.asString(),
                        maxLength = TEXT_AUTOCOLLAPSE_MAX_LENGTH,
                    )
                }

                reason?.let {
                    text(": ")
                    lineBreak()
                    emphasis { text(it) }
                }
            },
        code = BAD_FUNCTION_CALL_EXIT_CODE,
        function = call.function,
    )
