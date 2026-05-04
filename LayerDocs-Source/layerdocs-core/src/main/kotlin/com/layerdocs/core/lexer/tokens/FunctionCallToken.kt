package com.layerdocs.core.lexer.tokens

import com.layerdocs.core.lexer.Token
import com.layerdocs.core.lexer.TokenData
import com.layerdocs.core.parser.walker.WalkerParsingResult
import com.layerdocs.core.parser.walker.funcall.WalkedFunctionCall
import com.layerdocs.core.visitor.token.TokenVisitor

/**
 * A function call token, produced by the lexer's walker subsystem.
 * This is a custom LayerDocs element, and is both a block and inline node.
 *
 * Example:
 * ```
 * .function {arg1} {arg2}
 *     body
 * ```
 * The `body` argument is supported only when used as a block.
 * @param isBlock whether the function call is a block (opposite: inline)
 * @param walkerResult the result of the walker parsing, containing the structured [WalkedFunctionCall]
 * @see com.layerdocs.core.ast.layerdocs.FunctionCallNode
 */
class FunctionCallToken(
    data: TokenData,
    val isBlock: Boolean,
    val walkerResult: WalkerParsingResult<WalkedFunctionCall>,
) : Token(data) {
    override fun <T> accept(visitor: TokenVisitor<T>) = visitor.visit(this)
}
