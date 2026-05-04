package com.layerdocs.rendering.html.post.resources

import com.layerdocs.core.context.Context
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.document.sub.getOutputFileName
import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.output.TextOutputArtifact
import com.layerdocs.core.util.Escape
import java.io.StringWriter
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

private const val SITEMAP_FILE_NAME = "sitemap.xml"

@Suppress("HttpUrlsUsage")
private const val SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9"

/**
 * A [PostRendererResource] that generates a `sitemap.xml` listing the root document
 * and every subdocument as a `<url><loc>` entry with absolute URLs.
 *
 * Requires [com.layerdocs.core.context.options.HtmlOptions.baseUrl] to be set via `.htmloptions`;
 * if absent, no sitemap is emitted. The sitemap is also skipped when the document has no subdocuments.
 *
 * @param context the context of the document being rendered, used to access the base URL,
 *        subdocument graph, and output naming strategy
 */
class SitemapPostRendererResource(
    private val context: Context,
) : PostRendererResource {
    private val subdocuments: Map<Subdocument, Context>
        get() = context.sharedSubdocumentsData.withContexts

    override val runsInPreviewMode: Boolean = false

    override fun includeTo(
        resources: MutableSet<OutputResource>,
        rendered: CharSequence,
    ) {
        if (context.options.html.baseUrl == null) return
        if (subdocuments.keys.none { it !== Subdocument.Root }) return

        resources +=
            TextOutputArtifact(
                name = SITEMAP_FILE_NAME,
                content = buildSitemap(),
                type = ArtifactType.AUTO,
            )
    }

    private fun buildSitemap(): String {
        val baseUrl =
            context.options.html.baseUrl!!
                .trimEnd('/')

        val writer = StringWriter()
        val xml = XMLOutputFactory.newInstance().createXMLStreamWriter(writer)

        xml.writeStartDocument("UTF-8", "1.0")
        xml.writeStartElement("urlset")
        xml.writeDefaultNamespace(SITEMAP_NAMESPACE)

        // Root document.
        writeUrl(xml, baseUrl)

        // Subdocuments.
        subdocuments
            .asSequence()
            .filter { (subdocument, _) -> subdocument !== Subdocument.Root }
            .forEach { (subdocument, subdocumentContext) ->
                val directoryName = subdocument.getOutputFileName(subdocumentContext).let(Escape.Url::escape)
                writeUrl(xml, "$baseUrl/$directoryName")
            }

        xml.writeEndElement()
        xml.writeEndDocument()
        xml.flush()

        return writer.toString()
    }

    private fun writeUrl(
        xml: XMLStreamWriter,
        absoluteUrl: String,
    ) {
        xml.writeStartElement("url")
        xml.writeStartElement("loc")
        xml.writeCharacters(absoluteUrl)
        xml.writeEndElement()
        xml.writeEndElement()
    }
}
