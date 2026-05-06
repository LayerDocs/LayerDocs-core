package com.layerdocs.rendering.html.pdf

import com.layerdocs.core.document.sub.getOutputFileName
import com.layerdocs.core.pipeline.output.BinaryOutputArtifact
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.output.OutputResourceGroup
import com.layerdocs.core.pipeline.output.visitor.copy
import com.layerdocs.core.pipeline.output.visitor.saveTo
import com.layerdocs.core.rendering.PostRenderer
import com.layerdocs.rendering.html.post.HtmlPostRenderer
import java.io.File

/**
 * Decorator for [HtmlPostRenderer] that generates a PDF file from the HTML output via Puppeteer.
 * @param postRenderer the original [HtmlPostRenderer] to be decorated
 * @param options options for the PDF export process
 */
class PdfHtmlPostRendererDecorator(
    private val postRenderer: HtmlPostRenderer,
    private val options: HtmlPdfExportOptions,
) : PostRenderer by postRenderer {
    override fun generateResources(rendered: CharSequence): Set<OutputResource> {
        val outName = postRenderer.context.subdocument.getOutputFileName(postRenderer.context)
        val tempDirectory =
            kotlin.io.path
                .createTempDirectory(prefix = "layerdocs-pdf")
                .toFile()
        val out: File = tempDirectory.resolve("$outName.pdf")

        if (options.combine) {
            FragmentedPdfExporter(postRenderer.context, options, postRenderer::generateResources).export(out)
        } else {
            val resources = postRenderer.generateResources(rendered)
            val sourcesDirectory: File = OutputResourceGroup("sources", resources).saveTo(tempDirectory)
            HtmlPdfExporter(options).export(sourcesDirectory, out)
        }

        // In order to comply with the pipeline's contract, the output PDF is wrapped in an OutputResource.
        // It is deleted along with its temporary directory, and will be recreated in the output directory
        // by the pipeline's final process.
        return out
            .takeIf { it.exists() }
            ?.let(BinaryOutputArtifact::fromFile)
            .also { tempDirectory.deleteRecursively() }
            ?.let(::setOf)
            ?: emptySet()
    }

    override fun wrapResources(
        name: String,
        resources: Set<OutputResource>,
    ): OutputResource {
        // Single output file.
        resources.singleOrNull()?.let {
            return it.copy(name = "$name.pdf")
        }
        // Multiple output files (e.g. subdocuments).
        return OutputResourceGroup(
            name = name,
            resources = resources,
        )
    }
}
