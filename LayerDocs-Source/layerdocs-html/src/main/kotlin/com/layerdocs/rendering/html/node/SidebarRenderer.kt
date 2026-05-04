package com.layerdocs.rendering.html.node

import com.layerdocs.core.ast.attributes.id.getId
import com.layerdocs.core.ast.layerdocs.block.toc.TableOfContentsView
import com.layerdocs.core.ast.layerdocs.block.toc.convertTableOfContentsToListNode
import com.layerdocs.core.context.Context
import com.layerdocs.rendering.html.HtmlIdentifierProvider

private const val MAX_DEPTH = 3

/**
 * Renderer of the sidebar content, loaded from the document's table of contents,
 * to be injected into the HTML template.
 */
object SidebarRenderer {
    /**
     * Renders the sidebar content.
     * @return rendered sidebar content
     */
    fun render(context: Context): CharSequence {
        val toc = context.attributes.tableOfContents ?: return ""
        val renderer = LayerDocsHtmlNodeRenderer(context)
        val view = TableOfContentsView(maxDepth = MAX_DEPTH)
        val list =
            convertTableOfContentsToListNode(
                view,
                renderer,
                items = toc.items,
                linkUrlMapper = { item ->
                    "#" + HtmlIdentifierProvider.of(renderer).getId(item.target)
                },
            )

        return list.accept(renderer)
    }
}
