package com.layerdocs.stdlib

import com.layerdocs.core.ast.InlineMarkdownContent
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.LineBreak
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.TextTransformData
import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.library.module.moduleOf
import com.layerdocs.core.function.reflect.annotation.LikelyBody
import com.layerdocs.core.function.reflect.annotation.LikelyNamed
import com.layerdocs.core.function.reflect.annotation.Name
import com.layerdocs.core.function.value.NodeValue
import com.layerdocs.core.function.value.data.EvaluableString
import com.layerdocs.core.function.value.data.Range
import com.layerdocs.core.function.value.wrappedAsValue
import com.layerdocs.core.misc.color.Color
import com.layerdocs.core.util.node.toPlainText

/**
 * `Text` stdlib module exporter.
 * This module handles text formatting.
 */
val Text: LayerDocsModule =
    moduleOf(
        ::text,
        ::lineBreak,
        ::code,
        ::codeSpan,
        ::loremIpsum,
    )

/**
 * Creates an inline text node with specified formatting and transformation.
 *
 * @param text inline content to transform
 * @param size font size, or default if not specified
 * @param weight font weight, or default if not specified
 * @param style font style, or default if not specified
 * @param decoration text decoration, or default if not specified
 * @param case text case, or default if not specified
 * @param variant font variant, or default if not specified
 * @param script vertical script position (`sub` for subscript, `sup` for superscript), or default if not specified
 * @param color text color, or default if not specified
 * @param url optional URL to link the text to. If empty (but specified), the URL will match the text content
 * @param className CSS class name to apply to the element, if supported by the renderer. None if not specified.
 */
fun text(
    text: InlineMarkdownContent,
    @LikelyNamed size: TextTransformData.Size? = null,
    @LikelyNamed weight: TextTransformData.Weight? = null,
    @LikelyNamed style: TextTransformData.Style? = null,
    @LikelyNamed decoration: TextTransformData.Decoration? = null,
    @LikelyNamed case: TextTransformData.Case? = null,
    @LikelyNamed variant: TextTransformData.Variant? = null,
    @LikelyNamed script: TextTransformData.Script? = null,
    @LikelyNamed color: Color? = null,
    @LikelyNamed url: String? = null,
    @Name("classname") className: String? = null,
): NodeValue {
    val transform =
        TextTransform(
            TextTransformData(size, weight, style, decoration, case, variant, script, color),
            className,
            text.children,
        )

    return when {
        // If URL is specified, wrap the text in a link
        url != null -> {
            Link(
                listOf(transform),
                url = url.takeIf { it.isNotBlank() } ?: text.children.toPlainText(),
                title = null,
            )
        }

        else -> {
            transform
        }
    }.wrappedAsValue()
}

/**
 * Creates a line break. In standard Markdown, this is also achievable with two spaces at the end of a line,
 * but this function provides a more explicit and unambiguous way to insert a line break.
 * @return a [LineBreak] node
 */
@Name("br")
fun lineBreak() = LineBreak.wrappedAsValue()

/**
 * Creates a code block. Contrary to its standard Markdown implementation with backtick/tilde fences,
 * this function accepts function calls within its [code] argument,
 * hence it can be used - for example - in combination with [read] to load code from file.
 *
 * Example of a code block loaded from file via [read]:
 *
 * ```
 * .code lang:{kotlin} focus:{2..5}
 *     .read {snippet.kt}
 * ```
 *
 * @param language optional language of the code
 * @param caption optional caption
 * @param showLineNumbers whether to show line numbers
 * @param focusedLines range of lines to focus on. No lines are focused if unset. Supports open ranges.
 * Note: HTML rendering requires [showLineNumbers] to be enabled.
 * @param referenceId optional identifier for cross-referencing this code block elsewhere via [reference]
 * @param code code content
 */
fun code(
    @Name("lang") language: String? = null,
    @LikelyNamed caption: InlineMarkdownContent? = null,
    @Name("linenumbers") showLineNumbers: Boolean = true,
    @Name("focus") focusedLines: Range? = null,
    @Name("ref") referenceId: String? = null,
    @LikelyBody code: EvaluableString,
): NodeValue =
    Code(
        content = code.content,
        language = language,
        showLineNumbers = showLineNumbers,
        focusedLines = focusedLines,
        caption = caption?.children,
        referenceId = referenceId,
    ).wrappedAsValue()

/**
 * Creates an inline code span.
 * Equivalent to backticks in standard Markdown, but also accepts function calls within its [text] argument.
 * @param text code content
 * @return a [CodeSpan] node
 */
@Name("codespan")
fun codeSpan(text: String) = CodeSpan(text).wrappedAsValue()

/**
 * @return a fixed Lorem Ipsum text.
 */
@Name("loremipsum")
fun loremIpsum() =
    object {}::class.java
        .getResourceAsStream("/text/lorem-ipsum.txt")!!
        .reader()
        .readText()
        .wrappedAsValue()
