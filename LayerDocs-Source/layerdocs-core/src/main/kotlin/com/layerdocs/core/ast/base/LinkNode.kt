package com.layerdocs.core.ast.base

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.attributes.error.ErrorCapableNode
import com.layerdocs.core.context.file.FileSystem

/**
 * A general link node.
 * A link can error for various reasons.
 * For example, a [com.layerdocs.core.ast.base.inline.SubdocumentLink] can error
 * if the linked subdocument cannot be found.
 *
 * @see com.layerdocs.core.ast.base.inline.Link
 * @see com.layerdocs.core.ast.base.block.LinkDefinition
 */
interface LinkNode : ErrorCapableNode {
    /**
     * Inline content of the displayed label.
     */
    val label: InlineContent

    /**
     * URL this link points to.
     */
    val url: String

    /**
     * Optional title.
     */
    val title: InlineContent?

    /**
     * Optional file system where this link is defined, used for resolving relative paths.
     * @see com.layerdocs.core.context.hooks.LinkUrlResolverHook
     */
    val fileSystem: FileSystem?

    /**
     * Creates a copy of this link with the given [url].
     */
    fun copy(url: String): LinkNode
}
