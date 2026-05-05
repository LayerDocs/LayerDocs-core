package com.layerdocs.cli.renderer

import com.layerdocs.cli.CliOptions
import com.layerdocs.core.context.Context
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.rendering.RenderingComponents
import com.layerdocs.rendering.html.HtmlExportOptions
import com.layerdocs.rendering.html.extension.html
import com.layerdocs.rendering.html.extension.htmlPdf
import com.layerdocs.rendering.html.pdf.HtmlPdfExportOptions
import com.layerdocs.rendering.plaintext.extension.plainText

private const val HTML = "html"
private const val HTML_PDF = "html-pdf"
private const val PLAIN_TEXT = "text"
private const val SLIDES = "slides"

/**
 * Given a [CliOptions] instance, retrieves the appropriate renderer (e.g. HTML, PDF) for the pipeline
 * based on [CliOptions.rendererName] (case-insensitive), [CliOptions.exportPdf] and other options.
 */
class RendererRetriever(
    private val options: CliOptions,
) {
    private val name
        get() = options.rendererName.lowercase()

    /**
     * Retrieves the rendering target specified by [options].
     *
     * Note: the current implementation hardcodes renderer names. In the future an extensible retriever will be implemented.
     * @return the rendering target for the pipeline, to generate the output for.
     */
    fun getRenderer(): (RendererFactory, Context) -> RenderingComponents =
        { factory, context ->
            when {
                isHtmlPdf() -> factory.htmlPdf(context, createHtmlPdfExportOptions(), createHtmlExportOptions())
                isHtml() || isSlides() -> factory.html(context, createHtmlExportOptions())
                isPlainText() -> factory.plainText(context)
                else -> throw IllegalArgumentException("Unsupported renderer: '${options.rendererName}'")
            }
        }

    private fun isHtml() = name == HTML

    private fun isHtmlPdf() = name == HTML_PDF || (name == HTML && options.exportPdf)

    private fun isPlainText() = name == PLAIN_TEXT
    private fun isSlides() = name == SLIDES

    private fun createHtmlExportOptions() = HtmlExportOptions()

    private fun createHtmlPdfExportOptions() =
        HtmlPdfExportOptions(
            outputDirectory = requireNotNull(options.outputDirectory) { "Output directory must be specified for PDF export." },
            nodeJsPath = options.nodePath,
            npmPath = options.npmPath,
            noSandbox = options.noPdfSandbox,
        )
}
