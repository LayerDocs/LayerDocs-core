package com.layerdocs.rendering.html.post.document

import com.layerdocs.core.context.Context
import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.document.layout.font.FontInfo
import com.layerdocs.core.document.layout.page.PageFormatInfo
import com.layerdocs.core.document.layout.paragraph.ParagraphStyleInfo
import com.layerdocs.core.misc.font.FontFamily
import com.layerdocs.rendering.html.css.CssPageSelectors
import com.layerdocs.rendering.html.css.asCSS
import com.layerdocs.rendering.html.css.stylesheet

/**
 * Generates the CSS `<style>` content for an HTML document, including font-face imports,
 * CSS custom properties, and layout rules derived from the document's configuration.
 *
 * This class encapsulates all CSS/font concerns, keeping the [HtmlDocumentBuilder] focused
 * purely on HTML structure.
 *
 * @param context the rendering context containing document metadata, layout, and font configuration
 */
class HtmlDocumentStylesheet(
    private val context: Context,
    private val pageFormats: List<PageFormatInfo>,
) {
    private val document = context.documentInfo

    fun build(): String =
        buildString {
            appendLine(buildFonts())
            appendLine(buildParagraphStyle(document.layout.paragraphStyle))

            pageFormats.forEach { pageFormat ->
                appendLine(buildPageFormat(pageFormat))
            }
        }

    /**
     * Builds `@font-face` declarations and font CSS custom properties.
     * Emitted once, outside the per-format loop, since fonts are document-global
     * and CSS custom properties are invalid inside `@page` at-rules.
     */
    private fun buildFonts(): String =
        stylesheet {
            raw(fontFaceSnippets().joinToString("\n"))

            rule("body") {
                "--ld-main-custom-font" value mainFontFamily()
                "--ld-heading-custom-font" value headingFontFamily()
                "--ld-code-custom-font" value codeFontFamily()
                "--ld-main-font-size" value fontSizeCss()
            }
        }

    private fun buildParagraphStyle(paragraphStyle: ParagraphStyleInfo): String =
        stylesheet {
            rule("body") {
                "--ld-line-height" value paragraphStyle.lineHeight?.toString()
                "--ld-letter-spacing" value paragraphStyle.letterSpacing?.let { "${it}em" }
                "--ld-paragraph-vertical-margin" value paragraphStyle.spacing?.let { "${it}em" }
            }
            rule("p") {
                "--ld-paragraph-text-indent" value paragraphStyle.indent?.let { "${it}em" }
            }
        }

    /**
     * Builds the CSS stylesheet for a single [PageFormatInfo], depending on its scope ([PageFormatInfo.selector]) and properties.
     */
    private fun buildPageFormat(format: PageFormatInfo): String {
        val isScoped = format.selector != null

        // For scoped selectors, expand into individual @page selectors.
        val pageSelectors = CssPageSelectors.toCss(format.selector)

        return stylesheet {
            for (selector in if (isScoped) pageSelectors else listOf("body")) {
                rule(selector) {
                    "--ld-content-width" value format.pageWidth
                    "--ld-column-count" value format.columnCount?.toString()

                    if (format.alignment?.isLocal == true) {
                        "--ld-horizontal-alignment-local" importantValue format.alignment
                        "--ld-horizontal-alignment-global" importantValue "unset"
                        "--ld-horizontal-alignment-list-items" importantValue "unset"
                    }
                    if (format.alignment?.isGlobal == true) {
                        "--ld-horizontal-alignment-global" importantValue format.alignment
                        "--ld-horizontal-alignment-local" importantValue format.alignment
                    }

                    format.contentBorderWidth?.let {
                        "--ld-page-content-border-width" value it
                        "--ld-page-content-border-style" value "solid"
                    }
                    format.contentBorderColor?.let {
                        "--ld-page-content-border-color" value it
                        "--ld-page-content-border-style" value "solid"
                    }
                }
            }

            if (!isScoped) {
                rule(
                    "body.layerdocs-plain.layerdocs-plain",
                    "body.layerdocs-docs.layerdocs-docs",
                ) {
                    "margin" value format.margin
                }

                rule("body.layerdocs-slides.layerdocs-slides .reveal") {
                    "width" value format.pageWidth
                    "height" value format.pageHeight
                }
            }

            for (selector in pageSelectors) {
                rule(selector) {
                    if (format.pageWidth != null || format.pageHeight != null) {
                        "size" value "${format.pageWidth?.asCSS ?: "auto"} ${format.pageHeight?.asCSS ?: "auto"}"
                    }
                    "margin" value (format.margin?.asCSS ?: if (document.type == DocumentType.PLAIN) "0" else null)
                }
            }
        }
    }

    /**
     * Extracts font family IDs from the document's font stack using the given [extractor],
     * returning them as a comma-separated CSS value (e.g. `'Roboto', 'Noto Sans'`).
     * Fonts are reversed so that later declarations take higher priority.
     */
    private fun fontFamilyIds(extractor: (FontInfo) -> FontFamily?): String? =
        document.layout.fonts
            .reversed()
            .mapNotNull { extractor(it)?.id }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ") { "'$it'" }

    private fun mainFontFamily(): String? = fontFamilyIds { it.mainFamily }

    private fun headingFontFamily(): String? = fontFamilyIds { it.headingFamily }

    private fun codeFontFamily(): String? = fontFamilyIds { it.codeFamily }

    private fun fontSizeCss(): String? =
        document.layout.fonts
            .lastOrNull { it.size != null }
            ?.size
            ?.asCSS

    /** Generates `@font-face` CSS snippets for all font families referenced by the document. */
    private fun fontFaceSnippets(): List<String> {
        val allFamilies: List<FontFamily> =
            document.layout.fonts.flatMap { font ->
                listOfNotNull(font.mainFamily, font.headingFamily, font.codeFamily)
            }
        return CssFontFacesImporter(allFamilies, context.mediaStorage).toSnippets()
    }
}
