package com.layerdocs.core.function.reflect

import com.layerdocs.core.function.Function
import com.layerdocs.core.function.FunctionParameter
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.call.binding.ArgumentBindings
import com.layerdocs.core.function.call.validate.FunctionCallValidator
import com.layerdocs.core.function.error.FunctionCallRuntimeException
import com.layerdocs.core.function.reflect.annotation.Injected
import com.layerdocs.core.function.reflect.annotation.Name
import com.layerdocs.core.function.reflect.annotation.NoAutoArgumentUnwrapping
import com.layerdocs.core.function.reflect.annotation.NotForDocumentType
import com.layerdocs.core.function.reflect.annotation.OnlyForDocumentType
import com.layerdocs.core.function.reflect.annotation.toValidator
import com.layerdocs.core.function.value.InputValue
import com.layerdocs.core.function.value.None
import com.layerdocs.core.function.value.OutputValue
import com.layerdocs.core.log.Log
import com.layerdocs.core.pipeline.error.PipelineException
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * A LayerDocs [Function] adapted from a regular Kotlin [KFunction].
 * @param function Kotlin function to adapt
 */
class KFunctionAdapter<T : OutputValue<*>>(
    private val function: KFunction<T>,
) : Function<T> {
    /**
     * If the [Name] annotation is present on [function], the LayerDocs function name is set from there.
     * Otherwise, it is [function]'s original name.
     */
    override val name: String
        get() = function.findAnnotation<Name>()?.name ?: function.name

    @Suppress("UNCHECKED_CAST")
    override val parameters: List<FunctionParameter<*>>
        get() =
            function.parameters.map {
                FunctionParameter(
                    // If @Name is present, a custom name is set.
                    name = it.findAnnotation<Name>()?.name ?: it.name ?: "<unnamed parameter>",
                    type = it.type.classifier as KClass<out InputValue<T>>,
                    index = it.index,
                    isOptional = it.isOptional,
                    isInjected = it.hasAnnotation<Injected>(),
                    isNullable = it.type.isMarkedNullable,
                )
            }

    override val validators: List<FunctionCallValidator<T>>
        get() =
            buildList {
                function.findAnnotation<OnlyForDocumentType>()?.toValidator<T>()?.let(::add)
                function.findAnnotation<NotForDocumentType>()?.toValidator<T>()?.let(::add)
            }

    override val invoke: (ArgumentBindings, FunctionCall<T>) -> T
        get() = { bindings, call ->
            val args =
                bindings.asSequence().associate { (parameter, argument) ->
                    // Corresponding KParameter.
                    val param = function.parameters[parameter.index]

                    // The argument is unwrapped unless the value class specifies not to.
                    // An example of a disabled unwrapping is DynamicValue, which is used to pass dynamically typed values as-is.
                    val arg =
                        argument.value.let {
                            if (it::class.hasAnnotation<NoAutoArgumentUnwrapping>()) it else it.unwrappedValue
                        }

                    // LayerDocs's None becomes Kotlin's null for nullable parameters.
                    param to arg.takeUnless { arg is None && param.type.isMarkedNullable }
                }

            // Call the KFunction.
            try {
                function.callBy(args)
            } catch (e: InvocationTargetException) {
                // Exceptions thrown within the called function are converted to LayerDocs exceptions
                // and handled accordingly by the pipeline's function expander component.
                Log.debug("(expected, received): " + args.map { it.key.type to it.value })

                // If the exception comes from a nested function call, it is rethrown to go up the stack.
                throw e.targetException as? PipelineException
                    ?: FunctionCallRuntimeException(call, e.targetException)
            }
        }
}
