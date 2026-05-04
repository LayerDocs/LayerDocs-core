package com.layerdocs.core.function.reflect

import com.layerdocs.core.ast.layerdocs.FunctionCallNode
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.FunctionParameter
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.value.InputValue
import com.layerdocs.core.function.value.ObjectValue
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Utility for injected argument values.
 * @see FunctionParameter.isInjected
 * @see com.layerdocs.core.function.call.binding.InjectedArgumentsBinder
 */
object InjectedValue {
    /**
     * Generates a value to inject to a function parameter that expects a type of [type].
     *
     * Supported types:
     * - [Context]: injects the context of the function call
     * - [FunctionCallNode]: injects the source node of the function call
     * - [FunctionCall]: injects the function call itself
     *
     * @param type type of the target parameter to inject value to.
     * @param call function call to extract injectable data from
     * @return the function-call-ready value to inject
     * @throws IllegalArgumentException if the target type is not injectable
     */
    fun fromType(
        type: KClass<*>,
        call: FunctionCall<*>,
    ): InputValue<*> =
        when {
            type.isSubclassOf(Context::class) -> ObjectValue(call.context)
            type.isSubclassOf(FunctionCallNode::class) -> ObjectValue(call.sourceNode)
            type.isSubclassOf(FunctionCall::class) -> ObjectValue(call)
            else -> throw IllegalArgumentException("Cannot inject a value to type $type")
        }
}
