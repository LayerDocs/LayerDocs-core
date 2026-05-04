package com.layerdocs.rendering.html

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.dsl.buildBlocks
import com.layerdocs.core.ast.dsl.buildInline
import com.layerdocs.core.attachMockPipeline
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.SubdocumentContext
import com.layerdocs.core.context.subdocument.subdocumentGraph
import com.layerdocs.core.context.toc.TableOfContents
import com.layerdocs.core.document.DocumentInfo
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.graph.DirectedGraph
import com.layerdocs.core.graph.Graph
import com.layerdocs.core.graph.VisitableOnceGraph
import com.layerdocs.rendering.html.search.SearchEntry
import com.layerdocs.rendering.html.search.SearchHeading
import com.layerdocs.rendering.html.search.SearchIndex
import com.layerdocs.rendering.html.search.SearchIndexGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [SearchIndexGenerator].
 */
class SearchIndexGeneratorTest {
    @Test
    fun `single subdocument`() {
        val context = MutableContext(LayerDocsFlavor, subdocument = Subdocument.Root)
        val graph: Graph<Subdocument> = DirectedGraph<Subdocument>().addVertex(Subdocument.Root)
        context.documentInfo =
            DocumentInfo(name = "Test Document", description = "A test document", keywords = listOf("test", "document"))
        context.attributes.root =
            AstRoot(
                buildBlocks {
                    paragraph { emphasis { text("Hello, World!") } }
                    paragraph { text("This is a test document.") }
                },
            )
        context.subdocumentGraph = VisitableOnceGraph(graph)
        context.attachMockPipeline()

        val index = SearchIndexGenerator.generate(context.sharedSubdocumentsData)

        assertEquals(
            SearchIndex(
                entries =
                    listOf(
                        SearchEntry(
                            url = "/",
                            title = "Test Document",
                            description = "A test document",
                            keywords = listOf("test", "document"),
                            content = "Hello, World!\n\nThis is a test document.",
                            headings = emptyList(),
                        ),
                    ),
            ),
            index,
        )
    }

    @Test
    fun `two subdocuments`() {
        val rootSubdoc = Subdocument.Root
        val childSubdoc = Subdocument.Resource(name = "child", path = "", content = "")

        val rootContext = MutableContext(LayerDocsFlavor, subdocument = rootSubdoc)
        val childContext = SubdocumentContext(parent = rootContext, subdocument = childSubdoc)

        val graph: Graph<Subdocument> =
            DirectedGraph<Subdocument>()
                .addVertex(rootSubdoc)
                .addVertex(childSubdoc)
                .addEdge(rootSubdoc, childSubdoc)

        rootContext.documentInfo =
            DocumentInfo(
                name = "Root Document",
                description = "The root document",
                keywords = listOf("root", "document"),
            )

        childContext.documentInfo =
            DocumentInfo(
                name = "Child Document",
                description = "A child document",
                keywords = listOf("child", "document"),
            )

        rootContext.attributes.root = AstRoot(buildBlocks { paragraph { text("Root content") } })
        childContext.attributes.root = AstRoot(buildBlocks { paragraph { text("Child content") } })

        rootContext.subdocumentGraph = VisitableOnceGraph(graph)
        rootContext.sharedSubdocumentsData = rootContext.sharedSubdocumentsData.addContext(childSubdoc, childContext)
        rootContext.attachMockPipeline()
        childContext.attachMockPipeline()

        val index = SearchIndexGenerator.generate(rootContext.sharedSubdocumentsData)

        assertEquals(
            SearchIndex(
                entries =
                    listOf(
                        SearchEntry(
                            url = "/",
                            title = "Root Document",
                            description = "The root document",
                            keywords = listOf("root", "document"),
                            content = "Root content",
                            headings = emptyList(),
                        ),
                        SearchEntry(
                            url = "/child",
                            title = "Child Document",
                            description = "A child document",
                            keywords = listOf("child", "document"),
                            content = "Child content",
                            headings = emptyList(),
                        ),
                    ),
            ),
            index,
        )
    }

    @Test
    fun `subdocument with headings`() {
        val context = MutableContext(LayerDocsFlavor, subdocument = Subdocument.Root)
        val graph: Graph<Subdocument> = DirectedGraph<Subdocument>().addVertex(Subdocument.Root)
        context.documentInfo =
            DocumentInfo(
                name = "Document with Headings",
                description = "A document that has headings",
                keywords = listOf("headings"),
            )
        context.attributes.tableOfContents =
            TableOfContents.generate(
                sequenceOf(
                    Heading(depth = 1, text = buildInline { text("Heading 1") }),
                    Heading(depth = 2, text = buildInline { text("Heading 2") }),
                ),
            )

        context.subdocumentGraph = VisitableOnceGraph(graph)
        context.attachMockPipeline()

        val index = SearchIndexGenerator.generate(context.sharedSubdocumentsData)

        assertEquals(
            SearchIndex(
                entries =
                    listOf(
                        SearchEntry(
                            url = "/",
                            title = "Document with Headings",
                            description = "A document that has headings",
                            keywords = listOf("headings"),
                            content = "",
                            headings =
                                listOf(
                                    SearchHeading(
                                        anchor = "heading-1",
                                        text = "Heading 1",
                                        level = 1,
                                    ),
                                    SearchHeading(
                                        anchor = "heading-2",
                                        text = "Heading 2",
                                        level = 2,
                                    ),
                                ),
                        ),
                    ),
            ),
            index,
        )
    }
}
