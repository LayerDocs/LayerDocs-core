package com.layerdocs.rendering.html.extension

import com.layerdocs.core.context.Context
import com.layerdocs.core.flavor.RendererFactoryVisitor
import com.layerdocs.core.flavor.base.BaseMarkdownRendererFactory
import com.layerdocs.core.flavor.layerdocs.LayerDocsRendererFactory
import com.layerdocs.core.rendering.NodeRenderer
import com.layerdocs.rendering.html.node.BaseHtmlNodeRenderer
import com.layerdocs.rendering.html.node.LayerDocsHtmlNodeRenderer

/**
 * Supplier of an HTML node renderer from the active renderer factory.
 */
class HtmlRendererFactoryVisitor(
    private val context: Context,
) : RendererFactoryVisitor<NodeRenderer> {
    override fun visit(factory: BaseMarkdownRendererFactory) = BaseHtmlNodeRenderer(context)

    override fun visit(factory: LayerDocsRendererFactory) = LayerDocsHtmlNodeRenderer(context)
}
