package com.layerdocs.core.function.value.output.node

import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.value.BooleanValue
import com.layerdocs.core.function.value.NoneValue
import com.layerdocs.core.function.value.NumberValue
import com.layerdocs.core.function.value.ObjectValue
import com.layerdocs.core.function.value.StringValue
import com.layerdocs.core.function.value.factory.ValueFactory

/**
 * Producer of block nodes from function output values.
 * @param context context of the function
 * @see NodeOutputValueVisitor
 */
class BlockNodeOutputValueVisitor(
    private val context: Context,
) : NodeOutputValueVisitor() {
    // Proxy used to convert inline values to block values.
    private val inline = InlineNodeOutputValueVisitor(context)

    /**
     * @return [this] node wrapped in a [Paragraph] block
     */
    private fun Node.inParagraph() = Paragraph(listOf(this))

    // Inline-to-block conversion.

    override fun visit(value: StringValue) = inline.visit(value).inParagraph()

    override fun visit(value: NumberValue) = inline.visit(value).inParagraph()

    override fun visit(value: BooleanValue) = inline.visit(value).inParagraph()

    override fun visit(value: ObjectValue<*>) = inline.visit(value).inParagraph()

    override fun visit(value: NoneValue) = inline.visit(value).inParagraph()

    // Raw Markdown code is parsed as blocks.
    override fun parseRaw(
        raw: String,
        context: Context?,
    ) = ValueFactory.blockMarkdown(raw, context ?: this.context).asNodeValue()
}
