package com.layerdocs.core.flavor.base

import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.flavor.RendererFactoryVisitor

/**
 * [BaseMarkdownFlavor] renderer factory.
 */
data object BaseMarkdownRendererFactory : RendererFactory {
    override fun <T> accept(visitor: RendererFactoryVisitor<T>): T = visitor.visit(this)
}
