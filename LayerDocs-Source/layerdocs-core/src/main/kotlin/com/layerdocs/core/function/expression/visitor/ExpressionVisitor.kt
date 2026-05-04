package com.layerdocs.core.function.expression.visitor

import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.expression.ComposedExpression
import com.layerdocs.core.function.expression.Expression
import com.layerdocs.core.function.value.BooleanValue
import com.layerdocs.core.function.value.DictionaryValue
import com.layerdocs.core.function.value.DynamicValue
import com.layerdocs.core.function.value.EnumValue
import com.layerdocs.core.function.value.GeneralCollectionValue
import com.layerdocs.core.function.value.InlineMarkdownContentValue
import com.layerdocs.core.function.value.LambdaValue
import com.layerdocs.core.function.value.MarkdownContentValue
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.NoneValue
import com.layerdocs.core.function.value.NumberValue
import com.layerdocs.core.function.value.ObjectValue
import com.layerdocs.core.function.value.OrderedCollectionValue
import com.layerdocs.core.function.value.PairValue
import com.layerdocs.core.function.value.StringValue
import com.layerdocs.core.function.value.UnorderedCollectionValue

/**
 * A visitor for different kinds of [Expression].
 * @param T output type of the `visit` methods
 * @see Expression
 * @see EvalExpressionVisitor
 * @see AppendExpressionVisitor
 */
interface ExpressionVisitor<T> {
    fun visit(value: StringValue): T

    fun visit(value: NumberValue): T

    fun visit(value: BooleanValue): T

    fun visit(value: OrderedCollectionValue<*>): T

    fun visit(value: UnorderedCollectionValue<*>): T

    fun visit(value: GeneralCollectionValue<*>): T

    fun visit(value: PairValue<*, *>): T

    fun visit(value: DictionaryValue<*>): T

    fun visit(value: EnumValue): T

    fun visit(value: ObjectValue<*>): T

    fun visit(value: MarkdownContentValue): T

    fun visit(value: InlineMarkdownContentValue): T

    fun visit(value: NodeValue): T

    fun visit(value: DynamicValue): T

    fun visit(value: LambdaValue): T

    fun visit(value: NoneValue): T

    fun visit(expression: FunctionCall<*>): T

    fun visit(expression: ComposedExpression): T
}
