package com.layerdocs.core.function.value

import com.layerdocs.core.function.expression.visitor.ExpressionVisitor
import com.layerdocs.core.function.value.output.OutputValueVisitor

/**
 * An immutable boolean [Value].
 */
data class BooleanValue(
    override val unwrappedValue: Boolean,
) : InputValue<Boolean>,
    OutputValue<Boolean> {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T = visitor.visit(this)

    override fun <O> accept(visitor: OutputValueVisitor<O>): O = visitor.visit(this)
}

/**
 * @return [this] boolean wrapped into a [BooleanValue]
 */
fun Boolean.wrappedAsValue() = BooleanValue(this)
