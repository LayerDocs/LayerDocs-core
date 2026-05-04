package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.Node
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A graph representing the relationships between [com.layerdocs.core.document.sub.Subdocument]s
 * within the document, stored in [com.layerdocs.core.context.Context.sharedSubdocumentsData].
 */
class SubdocumentGraph : Node {
    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
