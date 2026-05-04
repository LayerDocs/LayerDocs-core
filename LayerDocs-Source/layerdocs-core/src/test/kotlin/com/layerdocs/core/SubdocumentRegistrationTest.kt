package com.layerdocs.core

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.SubdocumentLink
import com.layerdocs.core.ast.base.inline.getSubdocument
import com.layerdocs.core.ast.dsl.buildBlock
import com.layerdocs.core.ast.dsl.buildInline
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.hooks.SubdocumentRegistrationHook
import com.layerdocs.core.context.hooks.UnresolvedSubdocumentException
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.permissions.MissingPermissionException
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.core.pipeline.error.BasePipelineErrorHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val RESOURCE_PATH = "src/test/resources/subdoc"

/**
 * Tests for subdocument registration from [SubdocumentLink].
 */
class SubdocumentRegistrationTest {
    private val context =
        object : MutableContext(LayerDocsFlavor) {
            override val permissions = setOf(Permission.GlobalRead)
        }

    private fun link1() =
        SubdocumentLink(
            Link(
                label = buildInline { text("Link") },
                url = "$RESOURCE_PATH/subdoc-1.qd",
                title = null,
            ),
        )

    private fun link2() =
        SubdocumentLink(
            Link(
                label = buildInline { text("Link") },
                url = "$RESOURCE_PATH/subdoc-2.qd",
                title = null,
            ),
        )

    private fun invalidLink() =
        SubdocumentLink(
            Link(
                label = buildInline { text("Invalid Link") },
                url = "$RESOURCE_PATH/nonexistent.qd",
                title = null,
            ),
        )

    private fun traverse(root: Node) {
        context.sharedSubdocumentsData =
            context.sharedSubdocumentsData.copy(graph = context.sharedSubdocumentsData.graph.addVertex(Subdocument.Root))
        ObservableAstIterator()
            .attach(SubdocumentRegistrationHook(context))
            .traverse(root as NestableNode)
    }

    @Test
    fun `root to 1`() {
        val link = link1()
        val root =
            buildBlock {
                root {
                    +link
                }
            }

        traverse(root)

        assertEquals(
            2,
            context.sharedSubdocumentsData.graph.vertices.size,
        )
        assertEquals(
            link.getSubdocument(context),
            context.sharedSubdocumentsData.graph
                .getNeighbors(Subdocument.Root)
                .single(),
        )
        assertNull(link.error)
    }

    @Test
    fun `root to 1 and 2`() {
        val link1 = link1()
        val link2 = link2()
        val root =
            buildBlock {
                root {
                    +link1
                    +link2
                }
            }

        traverse(root)

        assertEquals(
            3,
            context.sharedSubdocumentsData.graph.vertices.size,
        )
        assertEquals(
            2,
            context.sharedSubdocumentsData.graph
                .getNeighbors(Subdocument.Root)
                .count(),
        )
    }

    @Test
    fun `root to 1 twice`() {
        val link = link1()
        val root =
            buildBlock {
                root {
                    +link
                    +link
                }
            }

        traverse(root)

        assertEquals(
            2,
            context.sharedSubdocumentsData.graph.vertices.size,
        )
        assertEquals(
            1,
            context.sharedSubdocumentsData.graph
                .getNeighbors(Subdocument.Root)
                .count(),
        )
    }

    @Test
    fun `invalid link, no error handler`() {
        val link = invalidLink()
        val root =
            buildBlock {
                root {
                    +link
                }
            }

        assertFailsWith<UnresolvedSubdocumentException> {
            traverse(root)
        }
    }

    @Test
    fun `invalid link, with error handler`() {
        context.attachMockPipeline(
            options =
                PipelineOptions(
                    errorHandler = BasePipelineErrorHandler(),
                ),
        )

        val link = invalidLink()
        val root =
            buildBlock {
                root {
                    +link
                }
            }

        traverse(root)

        assertEquals(
            1,
            context.sharedSubdocumentsData.graph.vertices.size,
        )
        assertNotNull(link.error)
    }

    @Test
    fun `missing read permission, with error handler`() {
        val noPermissionsContext =
            object : MutableContext(LayerDocsFlavor) {
                override val permissions = emptySet<Permission>()
            }

        noPermissionsContext.attachMockPipeline(
            options =
                PipelineOptions(
                    errorHandler = BasePipelineErrorHandler(),
                ),
        )

        noPermissionsContext.sharedSubdocumentsData =
            noPermissionsContext.sharedSubdocumentsData.copy(
                graph = noPermissionsContext.sharedSubdocumentsData.graph.addVertex(Subdocument.Root),
            )

        val link = link1()
        val root =
            buildBlock {
                root {
                    +link
                }
            }

        ObservableAstIterator()
            .attach(SubdocumentRegistrationHook(noPermissionsContext))
            .traverse(root as NestableNode)

        assertEquals(
            1,
            noPermissionsContext.sharedSubdocumentsData.graph.vertices.size,
        )
        assertIs<MissingPermissionException>(link.error?.first)
    }
}
