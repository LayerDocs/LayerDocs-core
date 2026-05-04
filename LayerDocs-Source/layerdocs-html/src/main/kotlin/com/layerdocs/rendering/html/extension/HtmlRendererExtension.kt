package com.layerdocs.rendering.html.extension

import com.layerdocs.core.context.Context
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.rendering.RenderingComponents
import com.layerdocs.rendering.html.HtmlExportOptions
import com.layerdocs.rendering.html.pdf.HtmlPdfExportOptions
import com.layerdocs.rendering.html.pdf.PdfHtmlPostRendererDecorator
import com.layerdocs.rendering.html.post.HtmlPostRenderer
import com.layerdocs.rendering.html.post.HtmlSubdocumentPostRenderer

/**
 * The HTML rendering plug-in produces a browser-compatible document.
 *
 * - The root document comes with a full export which includes themes and scripts, and possibly media resources.
 * - Other subdocuments are exported to lightweight subdirectories, with possibly media resources.
 */
fun RendererFactory.html(
    context: Context,
    options: HtmlExportOptions = HtmlExportOptions(),
) = RenderingComponents(
    nodeRenderer = accept(HtmlRendererFactoryVisitor(context)),
    postRenderer =
        when (context.subdocument) {
            Subdocument.Root -> HtmlPostRenderer(context, options.resourcesLayout)
            else -> HtmlSubdocumentPostRenderer(context)
        },
)

/**
 * The HTML-PDF rendering plug-in produces a PDF document from the HTML output of [html].
 * The outcome is 1:1 with what would be displayed in a Chrome browser.
 */
fun RendererFactory.htmlPdf(
    context: Context,
    pdfOptions: HtmlPdfExportOptions,
    htmlOptions: HtmlExportOptions,
) = RenderingComponents(
    nodeRenderer = accept(HtmlRendererFactoryVisitor(context)),
    postRenderer =
        PdfHtmlPostRendererDecorator(
            HtmlPostRenderer(context, htmlOptions.resourcesLayout),
            options = pdfOptions,
        ),
)
