package com.layerdocs.core.function.expression.visitor

import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.expression.ComposedExpression
import com.layerdocs.core.function.expression.append
import com.layerdocs.core.function.expression.eval
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
import com.layerdocs.core.function.value.Value

/**
 * An [ExpressionVisitor] that evaluates an expression into a single static value,
 * which can be used as an input for another function call.
 */
class EvalExpressionVisitor : ExpressionVisitor<Value<*>> {
    // Static values: the evaluation is the value itself.
    override fun visit(value: StringValue) = value

    override fun visit(value: NumberValue) = value

    override fun visit(value: BooleanValue) = value

    override fun visit(value: OrderedCollectionValue<*>) = value

    override fun visit(value: UnorderedCollectionValue<*>) = value

    override fun visit(value: GeneralCollectionValue<*>) = value

    override fun visit(value: PairValue<*, *>) = value

    override fun visit(value: DictionaryValue<*>) = value

    override fun visit(value: EnumValue) = value

    override fun visit(value: ObjectValue<*>) = value

    override fun visit(value: MarkdownContentValue) = value

    override fun visit(value: InlineMarkdownContentValue) = value

    override fun visit(value: NodeValue) = value

    override fun visit(value: DynamicValue) = value

    override fun visit(value: LambdaValue) = value

    override fun visit(value: NoneValue) = value

    // When used as an input value for another function call,
    // the output type of the function call must be an InputValue.
    override fun visit(expression: FunctionCall<*>) = expression.execute()

    override fun visit(expression: ComposedExpression): Value<*> {
        if (expression.expressions.isEmpty()) {
            throw IllegalStateException("Composed expression has no sub-expressions")
        }

        // Creates a single expression out of multiple ones
        // by appending them to each other.
        var merged = expression.expressions.first()
        expression.expressions.asSequence().drop(1).forEach {
            merged = merged.append(it)
        }

        // The value of the built expression.
        return merged.eval()
    }
}
