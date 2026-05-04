package com.layerdocs.core.visitor.token

import com.layerdocs.core.lexer.tokens.CodeSpanToken
import com.layerdocs.core.lexer.tokens.CommentToken
import com.layerdocs.core.lexer.tokens.CriticalContentToken
import com.layerdocs.core.lexer.tokens.DiamondAutolinkToken
import com.layerdocs.core.lexer.tokens.EmphasisToken
import com.layerdocs.core.lexer.tokens.EntityToken
import com.layerdocs.core.lexer.tokens.EscapeToken
import com.layerdocs.core.lexer.tokens.ImageToken
import com.layerdocs.core.lexer.tokens.InlineMathToken
import com.layerdocs.core.lexer.tokens.LineBreakToken
import com.layerdocs.core.lexer.tokens.LinkToken
import com.layerdocs.core.lexer.tokens.PlainTextToken
import com.layerdocs.core.lexer.tokens.ReferenceFootnoteToken
import com.layerdocs.core.lexer.tokens.ReferenceImageToken
import com.layerdocs.core.lexer.tokens.ReferenceLinkToken
import com.layerdocs.core.lexer.tokens.StrikethroughToken
import com.layerdocs.core.lexer.tokens.StrongEmphasisToken
import com.layerdocs.core.lexer.tokens.StrongToken
import com.layerdocs.core.lexer.tokens.TextSymbolToken
import com.layerdocs.core.lexer.tokens.UrlAutolinkToken

/**
 * A visitor for inline [com.layerdocs.core.lexer.Token]s.
 * @param T output type of the `visit` methods
 */
interface InlineTokenVisitor<T> {
    fun visit(token: EscapeToken): T

    fun visit(token: EntityToken): T

    fun visit(token: CriticalContentToken): T

    fun visit(token: TextSymbolToken): T

    fun visit(token: CommentToken): T

    fun visit(token: LineBreakToken): T

    fun visit(token: LinkToken): T

    fun visit(token: ReferenceLinkToken): T

    fun visit(token: ReferenceFootnoteToken): T

    fun visit(token: DiamondAutolinkToken): T

    fun visit(token: UrlAutolinkToken): T

    fun visit(token: ImageToken): T

    fun visit(token: ReferenceImageToken): T

    fun visit(token: CodeSpanToken): T

    // Emphasis

    fun visit(token: PlainTextToken): T

    fun visit(token: EmphasisToken): T

    fun visit(token: StrongToken): T

    fun visit(token: StrongEmphasisToken): T

    fun visit(token: StrikethroughToken): T

    // LayerDocs extensions

    fun visit(token: InlineMathToken): T
}
