package com.layerdocs.core.ast.layerdocs.inline

import com.layerdocs.core.ast.base.inline.PlainTextNode
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A text-based symbol, such as `©`, `…`, `≥`.
 * This is usually the result of a combination of multiple characters (e.g. `(C)` -> `©`).
 * @param symbol processed symbol (e.g. `©`)
 * @see com.layerdocs.core.lexer.patterns.TextSymbolReplacement
 */
class TextSymbol(
    private val symbol: Char,
) : PlainTextNode {
    /**
     * @return [symbol] as a string
     */
    override val text: String
        get() = symbol.toString()

    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}
