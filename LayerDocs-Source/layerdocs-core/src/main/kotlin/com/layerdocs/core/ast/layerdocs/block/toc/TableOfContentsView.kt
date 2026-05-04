package com.layerdocs.core.ast.layerdocs.block.toc

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.AstAttributes
import com.layerdocs.core.context.toc.TableOfContents
import com.layerdocs.core.util.node.toPlainText
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * When this node is rendered, the current table of contents,
 * retrieved from the auto-generated [AstAttributes.tableOfContents], is displayed.
 * @param maxDepth maximum depth the table of contents to display.
 *                 For instance, if `maxDepth` is 2, only headings of level 1 and 2 will be displayed
 * @param focusedItem if not `null`, adds focus to the item of the table of contents with the same text content as this value
 */
class TableOfContentsView(
    val maxDepth: Int,
    private val focusedItem: InlineContent? = null,
) : Node {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)

    /**
     * @param item table of contents item to compare
     * @return whether the given item of a table of contents should be focused, according to the [focusedItem] property.
     *         Their pure text content (ignoring formatting) is compared.
     */
    fun hasFocus(item: TableOfContents.Item) =
        focusedItem != null &&
            item.text.toPlainText() == focusedItem.toPlainText()
}
