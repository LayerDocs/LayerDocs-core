package com.layerdocs.core.function.expression

import com.layerdocs.core.function.error.internal.InvalidExpressionEvalException
import com.layerdocs.core.function.expression.visitor.ExpressionVisitor
import com.layerdocs.core.function.value.factory.ValueFactory

/**
 * An [Expression] that, upon failed evaluation due to an [InvalidExpressionEvalException],
 * delegates the operation to a safe fallback expression.
 * @see ValueFactory.safeExpression
 */
class SafeExpression(
    val expression: Expression,
    fallback: () -> Expression,
) : Expression {
    private val lazyFallback by lazy(fallback)

    override fun <T> accept(visitor: ExpressionVisitor<T>): T =
        try {
            expression.accept(visitor)
        } catch (e: InvalidExpressionEvalException) {
            lazyFallback.accept(visitor)
        }
}
