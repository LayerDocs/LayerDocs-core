package com.layerdocs.core.function.error

import com.layerdocs.core.function.FunctionParameter
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.call.FunctionCallArgument
import com.layerdocs.core.function.call.asString

/**
 * An exception thrown if a function parameter is bound more than once in a function call.
 * @param call the invalid call
 * @param parameter the parameter that was attempted to be bound again
 * @param overridingArgument the argument that was attempted to be bound to the already bound parameter
 */
class ParameterAlreadyBoundException(
    call: FunctionCall<*>,
    parameter: FunctionParameter<*>,
    overridingArgument: FunctionCallArgument,
) : InvalidFunctionCallException(
        call,
        reason = "parameter '${parameter.name}' is already bound, but was attempted to be bound again to ${overridingArgument.asString()}",
    )
