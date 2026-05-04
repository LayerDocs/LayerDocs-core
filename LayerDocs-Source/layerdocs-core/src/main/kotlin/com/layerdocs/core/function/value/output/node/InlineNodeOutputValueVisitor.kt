package com.layerdocs.core.function.value.output.node

import com.layerdocs.core.ast.base.inline.CheckBox
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.value.BooleanValue
import com.layerdocs.core.function.value.NoneValue
import com.layerdocs.core.function.value.NumberValue
import com.layerdocs.core.function.value.ObjectValue
import com.layerdocs.core.function.value.StringValue
import com.layerdocs.core.function.value.factory.ValueFactory

/**
 * Producer of inline nodes from function output values.
 * @param context context of the function
 * @see NodeOutputValueVisitor
 */
class InlineNodeOutputValueVisitor(
    private val context: Context,
) : NodeOutputValueVisitor() {
    override fun visit(value: StringValue) = Text(value.unwrappedValue)

    override fun visit(value: NumberValue) = Text(value.unwrappedValue.toString())

    override fun visit(value: BooleanValue) = CheckBox(isChecked = value.unwrappedValue)

    override fun visit(value: ObjectValue<*>) = Text(value.unwrappedValue.toString())

    override fun visit(value: NoneValue) = CodeSpan(value.unwrappedValue.toString())

    // Raw Markdown code is parsed as inline.
    override fun parseRaw(
        raw: String,
        context: Context?,
    ) = ValueFactory.inlineMarkdown(raw, context ?: this.context).asNodeValue()
}
