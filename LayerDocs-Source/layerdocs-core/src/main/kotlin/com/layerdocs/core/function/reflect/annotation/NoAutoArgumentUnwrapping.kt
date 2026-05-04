package com.layerdocs.core.function.reflect.annotation

import com.layerdocs.core.function.reflect.KFunctionAdapter
import com.layerdocs.core.function.value.Value

/**
 * When invoking a function via [KFunctionAdapter], [Value] arguments are automatically unwrapped to their raw value,
 * unless this annotation is present on the [Value] subclass.
 */
@Target(AnnotationTarget.CLASS)
annotation class NoAutoArgumentUnwrapping
