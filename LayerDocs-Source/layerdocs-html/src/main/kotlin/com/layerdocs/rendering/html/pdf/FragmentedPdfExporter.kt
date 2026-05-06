package com.layerdocs.rendering.html.pdf

import com.layerdocs.core.ast.fragment.AstFragmenter
import com.layerdocs.core.context.Context
import com.layerdocs.core.log.Log
import com.layerdocs.interaction.executable.NodeJsWrapper
import com.layerdocs.interaction.executable.NodeModuleNotInstalledException
import com.layerdocs.interaction.executable.NpmWrapper
import com.layerdocs.core.pipeline.output.visitor.saveTo
import com.layerdocs.rendering.html.node.SidebarRenderer
import com.layerdocs.rendering.html.post.document.HtmlDocumentBuilder
import java.io.File

class FragmentedPdfExporter(
    private val context: Context,
    private val options: HtmlPdfExportOptions,
    private val resourcesProvider: (CharSequence) -> Set<com.layerdocs.core.pipeline.output.OutputResource>,
) {
    fun export(out: File) {
        val root = context.attributes.root ?: return
        val fragments = AstFragmenter.fragment(root.children)
        
        if (fragments.isEmpty()) return
        
        val tempDirectory = kotlin.io.path.createTempDirectory("layerdocs-fragments").toFile()
        try {
            val pdfFiles = mutableListOf<File>()
            
            // Get all assets (themes, scripts, etc.) once.
            // We pass empty content because we'll inject the fragmented HTML ourselves.
            val assets = resourcesProvider("").filter { 
                it is com.layerdocs.core.pipeline.output.OutputResourceGroup || 
                it.name != "index" // Avoid the empty index.html
            }.toSet()
            
            fragments.forEachIndexed { index, fragmentNodes ->
                val pageIndex = index + 1
                val pageDir = tempDirectory.resolve("page_$pageIndex")
                pageDir.mkdirs()
                
                // Render the fragment to HTML
                val htmlBuilder = HtmlDocumentBuilder(
                    context,
                    relativePathToRoot = ".",
                    sidebarContent = SidebarRenderer.render(context)
                )
                
                val fragmentHtml = htmlBuilder.build(renderFragment(fragmentNodes)).toString()
                val fixedHtml = injectHardA4Css(fragmentHtml)
                
                // Save assets and HTML to the page directory
                val htmlResource = com.layerdocs.core.pipeline.output.TextOutputArtifact(
                    name = "index",
                    content = fixedHtml,
                    type = com.layerdocs.core.pipeline.output.ArtifactType.HTML
                )
                
                val resources = assets + htmlResource
                val sourcesDirectory = com.layerdocs.core.pipeline.output.OutputResourceGroup("sources", resources)
                    .saveTo(pageDir)
                
                val pagePdf = tempDirectory.resolve("page_$pageIndex.pdf")
                HtmlPdfExporter(options).export(sourcesDirectory, pagePdf)
                
                if (pagePdf.exists()) {
                    pdfFiles.add(pagePdf)
                }
            }
            
            PdfMerger.merge(pdfFiles, out)
            
        } finally {
            tempDirectory.deleteRecursively()
        }
    }
    
    private fun renderFragment(nodes: List<com.layerdocs.core.ast.Node>): CharSequence {
        val renderer = com.layerdocs.rendering.html.node.LayerDocsHtmlNodeRenderer(context)
        return nodes.joinToString("") { it.accept(renderer) }
    }
    
    private fun injectHardA4Css(html: String): String {
        val hardA4Css = """
            <style>
                @media print {
                    body.layerdocs {
                        width: 210mm !important;
                        height: 297mm !important;
                        overflow: hidden !important;
                        margin: 0 !important;
                        padding: 0 !important;
                        display: block !important;
                    }
                    main {
                        margin: 0 !important;
                        padding: 20mm !important; /* Default padding for content */
                        border: none !important;
                        width: 100% !important;
                        height: 100% !important;
                        box-sizing: border-box !important;
                    }
                }
            </style>
        """.trimIndent()
        return html.replace("</head>", "$hardA4Css</head>")
    }
}
