package com.layerdocs.core.visitor.token

import com.layerdocs.core.lexer.tokens.BlockCodeToken
import com.layerdocs.core.lexer.tokens.BlockQuoteToken
import com.layerdocs.core.lexer.tokens.BlockTextToken
import com.layerdocs.core.lexer.tokens.FencesCodeToken
import com.layerdocs.core.lexer.tokens.FootnoteDefinitionToken
import com.layerdocs.core.lexer.tokens.FunctionCallToken
import com.layerdocs.core.lexer.tokens.HeadingToken
import com.layerdocs.core.lexer.tokens.HorizontalRuleToken
import com.layerdocs.core.lexer.tokens.HtmlToken
import com.layerdocs.core.lexer.tokens.LinkDefinitionToken
import com.layerdocs.core.lexer.tokens.ListItemToken
import com.layerdocs.core.lexer.tokens.MultilineMathToken
import com.layerdocs.core.lexer.tokens.NewlineToken
import com.layerdocs.core.lexer.tokens.OnelineMathToken
import com.layerdocs.core.lexer.tokens.OrderedListToken
import com.layerdocs.core.lexer.tokens.PageBreakToken
import com.layerdocs.core.lexer.tokens.ParagraphToken
import com.layerdocs.core.lexer.tokens.SetextHeadingToken
import com.layerdocs.core.lexer.tokens.TableToken
import com.layerdocs.core.lexer.tokens.UnorderedListToken

/**
 * A visitor for block [com.layerdocs.core.lexer.Token]s.
 * @param T output type of the `visit` methods
 */
interface BlockTokenVisitor<T> {
    fun visit(token: NewlineToken): T

    fun visit(token: BlockCodeToken): T

    fun visit(token: FencesCodeToken): T

    fun visit(token: HorizontalRuleToken): T

    fun visit(token: HeadingToken): T

    fun visit(token: SetextHeadingToken): T

    fun visit(token: LinkDefinitionToken): T

    fun visit(token: FootnoteDefinitionToken): T

    fun visit(token: UnorderedListToken): T

    fun visit(token: OrderedListToken): T

    fun visit(token: ListItemToken): T

    fun visit(token: TableToken): T

    fun visit(token: HtmlToken): T

    fun visit(token: ParagraphToken): T

    fun visit(token: BlockQuoteToken): T

    fun visit(token: BlockTextToken): T

    // LayerDocs extensions

    fun visit(token: PageBreakToken): T

    fun visit(token: MultilineMathToken): T

    fun visit(token: OnelineMathToken): T

    fun visit(token: FunctionCallToken): T
}
