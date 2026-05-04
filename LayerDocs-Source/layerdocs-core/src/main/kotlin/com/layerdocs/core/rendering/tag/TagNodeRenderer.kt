package com.layerdocs.core.rendering.tag

import com.layerdocs.core.ast.base.inline.CriticalContent
import com.layerdocs.core.context.Context
import com.layerdocs.core.rendering.NodeRenderer

/**
 * A converter of [com.layerdocs.core.ast.Node]s into tag-based output code,
 * by using a DSL-like approach provided by [TagBuilder].
 * @param context rendering context
 */
abstract class TagNodeRenderer<B : TagBuilder>(
    context: Context,
) : NodeRenderer(context) {
    /**
     * Whether the output code should be pretty.
     */
    val pretty: Boolean
        get() = context.attachedPipeline?.options?.prettyOutput ?: false

    /**
     * Factory method that creates a new builder.
     * @param name name of the tag to open
     * @param pretty whether the output code should be pretty
     */
    abstract fun createBuilder(
        name: String,
        pretty: Boolean,
    ): B

    /**
     * @param unescaped input to escape critical content for
     * @return the input string with the critical content escaped into safe content
     *         (e.g. in HTML `<` is escaped to `&lt;`).
     * @see CriticalContent
     */
    abstract fun escapeCriticalContent(unescaped: String): CharSequence

    /**
     * @see escapeCriticalContent
     */
    override fun visit(node: CriticalContent) = escapeCriticalContent(node.text)
}
