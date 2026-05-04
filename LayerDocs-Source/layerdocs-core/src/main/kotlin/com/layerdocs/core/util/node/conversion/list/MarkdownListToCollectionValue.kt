package com.layerdocs.core.util.node.conversion.list

import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.TextNode
import com.layerdocs.core.ast.base.block.list.ListBlock
import com.layerdocs.core.context.Context
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.OrderedCollectionValue
import com.layerdocs.core.function.value.OutputValue
import com.layerdocs.core.function.value.factory.ValueFactory
import com.layerdocs.core.util.node.toPlainText

/**
 * Helper that converts a Markdown list to an [OrderedCollectionValue].
 * @param list list to convert
 * @param inlineValueMapper function that maps the node of a list item to a value
 * @param nestedValueMapper function that maps a nested list item to a value.
 *        The first argument is the parent node, and the second is the nested [ListBlock]
 * @param T type of values in the collection
 * @see OrderedCollectionValue
 * @see ValueFactory.iterable
 */
class MarkdownListToCollectionValue<T : OutputValue<*>>(
    list: ListBlock,
    inlineValueMapper: (Node) -> T,
    nestedValueMapper: (Node, ListBlock) -> T,
) : MarkdownListToIterable<OrderedCollectionValue<T>, T>(list, inlineValueMapper, nestedValueMapper) {
    override fun wrap(): OrderedCollectionValue<T> = OrderedCollectionValue(elements.toList())

    companion object {
        /**
         * [MarkdownListToCollectionValue] factory via a [ValueFactory].
         * @param list list to convert
         * @param context context to use for the conversion
         */
        fun viaValueFactory(
            list: ListBlock,
            context: Context,
        ): MarkdownListToCollectionValue<*> =
            MarkdownListToCollectionValue(
                list,
                inlineValueMapper = {
                    when (it) {
                        is TextNode -> ValueFactory.eval(it.text.toPlainText(), context)
                        else -> NodeValue(it)
                    }
                },
                nestedValueMapper = { _, list -> viaValueFactory(list, context).convert() },
            )
    }
}
