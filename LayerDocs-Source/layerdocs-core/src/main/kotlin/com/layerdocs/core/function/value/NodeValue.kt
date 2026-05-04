package com.layerdocs.core.function.value

import com.layerdocs.core.ast.MarkdownContent
import com.layerdocs.core.ast.Node
import com.layerdocs.core.function.expression.Expression
import com.layerdocs.core.function.expression.visitor.ExpressionVisitor
import com.layerdocs.core.function.value.output.OutputValueVisitor

/**
 * An immutable [Node] [Value].
 */
data class NodeValue(
    override val unwrappedValue: Node,
) : OutputValue<Node>,
    Expression,
    AdaptableValue<MarkdownContentValue> {
    override fun <O> accept(visitor: OutputValueVisitor<O>): O = visitor.visit(this)

    override fun <T> accept(visitor: ExpressionVisitor<T>): T = visitor.visit(this)

    override fun adapt(): MarkdownContentValue = MarkdownContentValue(MarkdownContent(listOf(unwrappedValue)))
}

/**
 * @return [this] node wrapped into a [NodeValue]
 */
fun Node.wrappedAsValue() = NodeValue(this)
