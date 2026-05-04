package com.layerdocs.core.visitor.token

/**
 * A visitor for [com.layerdocs.core.lexer.Token]s.
 * @param T output type of the `visit` methods
 */
interface TokenVisitor<T> :
    BlockTokenVisitor<T>,
    InlineTokenVisitor<T>
