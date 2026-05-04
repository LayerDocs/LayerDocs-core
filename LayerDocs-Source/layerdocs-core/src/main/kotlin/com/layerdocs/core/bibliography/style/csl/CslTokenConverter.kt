package com.layerdocs.core.bibliography.style.csl

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.inline.Emphasis
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.Strong
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.TextTransformData
import de.undercouch.citeproc.csl.internal.TokenBuffer
import de.undercouch.citeproc.csl.internal.behavior.FormattingAttributes
import de.undercouch.citeproc.csl.internal.token.DisplayGroupToken
import de.undercouch.citeproc.csl.internal.token.TextToken

/**
 * Converts citeproc-java [TokenBuffer] tokens into LayerDocs [InlineContent] AST nodes.
 *
 * This is the bridge between citeproc-java's internal token representation and LayerDocs's AST,
 * mapping formatting attributes (italic, bold, small caps) and token types (URL, DOI) to
 * their corresponding LayerDocs node types.
 *
 * @param urlFormatter resolves raw URL/DOI text into display URLs
 *                     (e.g. prepending `https://doi.org/` to DOIs)
 */
internal class CslTokenConverter(
    private val urlFormatter: UrlFormatter,
) {
    /**
     * Resolves raw URL/DOI text into display URLs.
     */
    fun interface UrlFormatter {
        /**
         * @param text the raw URL or DOI text
         * @param type the token type (URL or DOI)
         * @return the formatted URL string
         */
        fun format(
            text: String,
            type: TextToken.Type,
        ): String
    }

    /**
     * Converts all tokens in a [TokenBuffer] into LayerDocs [InlineContent] AST nodes.
     *
     * [TextToken]s are converted based on their type and formatting attributes.
     * [DisplayGroupToken]s are skipped, as LayerDocs handles layout via CSS.
     */
    fun convert(buffer: TokenBuffer): InlineContent =
        buildList {
            for (token in buffer.tokens) {
                when (token) {
                    is TextToken -> if (token.text.isNotEmpty()) add(convertTextToken(token))
                    is DisplayGroupToken -> continue
                }
            }
        }

    /**
     * Extracts plain text from a [TokenBuffer], discarding formatting.
     */
    fun extractPlainText(buffer: TokenBuffer): String =
        buffer.tokens
            .filterIsInstance<TextToken>()
            .joinToString("") { it.text }
            .trim()

    /**
     * Converts a single [TextToken] into a LayerDocs AST node.
     *
     * URL/DOI tokens produce [Link] nodes. Other tokens produce [Text] nodes,
     * optionally wrapped in [Emphasis], [Strong], or [TextTransform]
     * based on their [FormattingAttributes].
     */
    private fun convertTextToken(token: TextToken): Node {
        if (token.type == TextToken.Type.URL || token.type == TextToken.Type.DOI) {
            return convertLinkToken(token.type, token.text)
        }

        return applyFormatting(Text(token.text), token.formattingAttributes)
    }

    /**
     * Converts a URL or DOI token into a [Link] node.
     */
    private fun convertLinkToken(
        type: TextToken.Type,
        text: String,
    ): Link {
        val url = urlFormatter.format(text, type)
        return Link(
            label = listOf(Text(url)),
            url = url,
            title = null,
        )
    }

    /**
     * Wraps a [node] in formatting nodes based on the given [FormattingAttributes] bitmask.
     * Attributes are applied innermost to outermost: italic, then bold, then small caps.
     */
    private fun applyFormatting(
        node: Node,
        attrs: Int,
    ): Node {
        var result = node

        if (FormattingAttributes.getFontStyle(attrs) == FormattingAttributes.FS_ITALIC) {
            result = Emphasis(text = listOf(result))
        }

        if (FormattingAttributes.getFontWeight(attrs) == FormattingAttributes.FW_BOLD) {
            result = Strong(text = listOf(result))
        }

        if (FormattingAttributes.getFontVariant(attrs) == FormattingAttributes.FV_SMALLCAPS) {
            result =
                TextTransform(
                    data = TextTransformData(variant = TextTransformData.Variant.SMALL_CAPS),
                    children = listOf(result),
                )
        }

        return result
    }
}
