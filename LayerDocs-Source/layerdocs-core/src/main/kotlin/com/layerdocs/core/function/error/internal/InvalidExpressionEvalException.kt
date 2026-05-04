package com.layerdocs.core.function.error.internal

import com.layerdocs.core.function.expression.ComposedExpression
import com.layerdocs.core.function.expression.Expression
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.factory.ValueFactory

/**
 * An exception thrown when an [Expression] cannot be evaluated.
 * Most commonly, this is thrown when a [NodeValue] appears in a [ComposedExpression],
 * hence the content must be parsed as Markdown instead of expression.
 * @see ValueFactory.eval
 */
class InvalidExpressionEvalException : Exception()
