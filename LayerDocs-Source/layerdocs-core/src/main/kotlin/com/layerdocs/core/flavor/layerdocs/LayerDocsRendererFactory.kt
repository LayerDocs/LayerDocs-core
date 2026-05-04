package com.layerdocs.core.flavor.layerdocs

import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.flavor.RendererFactoryVisitor

/**
 * [LayerDocsRendererFactory] renderer factory.
 */
class LayerDocsRendererFactory : RendererFactory {
    override fun <T> accept(visitor: RendererFactoryVisitor<T>): T = visitor.visit(this)
}
