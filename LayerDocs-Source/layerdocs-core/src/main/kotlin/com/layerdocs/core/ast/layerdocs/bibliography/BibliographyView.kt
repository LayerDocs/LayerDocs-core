package com.layerdocs.core.ast.layerdocs.bibliography

import com.layerdocs.core.ast.Node
import com.layerdocs.core.bibliography.Bibliography
import com.layerdocs.core.bibliography.style.BibliographyStyle
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Renderable container of a [bibliography].
 * @param bibliography the bibliography to render
 * @param style the style to use for rendering the bibliography
 */
class BibliographyView(
    val bibliography: Bibliography,
    val style: BibliographyStyle,
) : Node {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
