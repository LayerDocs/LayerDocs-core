package com.layerdocs.core.function.value.output.node

import com.layerdocs.core.ast.Node
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.value.output.OutputValueVisitor
import com.layerdocs.core.function.value.output.OutputValueVisitorFactory

/**
 * A factory that produces [OutputValueVisitor]s that map function output values
 * into [Node]s that can be appended to the AST.
 * @param context current context
 */
class NodeOutputValueVisitorFactory(
    private val context: Context,
) : OutputValueVisitorFactory<Node> {
    /**
     * @return a visitor that maps the output of a block function call into a block [Node]
     */
    override fun block(): OutputValueVisitor<Node> = BlockNodeOutputValueVisitor(context)

    /**
     * @return a visitor that maps the output of an inline function call into an inline [Node]
     */
    override fun inline(): OutputValueVisitor<Node> = InlineNodeOutputValueVisitor(context)
}
