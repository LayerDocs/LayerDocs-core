package com.layerdocs.rendering.plaintext

import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.OutputResourceGroup
import com.layerdocs.core.pipeline.output.TextOutputArtifact
import com.layerdocs.rendering.plaintext.post.PlainTextPostRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Tests for [PlainTextPostRenderer].
 */
class PlainTextPostRendererTest {
    @Test
    fun `resource generation`() {
        val postRenderer = PlainTextPostRenderer(MutableContext(LayerDocsFlavor))
        val resources = postRenderer.generateResources("Hello, World!\n\n")
        val resource = resources.single()
        assertIs<TextOutputArtifact>(resource)
        assertEquals("Hello, World!", resource.content)
    }

    @Test
    fun `single resource wrapping`() {
        val postRenderer = PlainTextPostRenderer(MutableContext(LayerDocsFlavor))
        val resource =
            postRenderer.wrapResources(
                name = "Hello",
                resources =
                    setOf(
                        TextOutputArtifact(
                            name = "output",
                            content = "Content",
                            type = ArtifactType.PLAIN_TEXT,
                        ),
                    ),
            )
        assertIs<TextOutputArtifact>(resource)
        assertEquals("Hello", resource.name)
    }

    @Test
    fun `multiple resource wrapping`() {
        val postRenderer = PlainTextPostRenderer(MutableContext(LayerDocsFlavor))
        val resource =
            postRenderer.wrapResources(
                name = "Group",
                resources =
                    setOf(
                        TextOutputArtifact(
                            name = "output1",
                            content = "Content 1",
                            type = ArtifactType.PLAIN_TEXT,
                        ),
                        TextOutputArtifact(
                            name = "output2",
                            content = "Content 2",
                            type = ArtifactType.PLAIN_TEXT,
                        ),
                    ),
            )
        assertEquals("Group", resource.name)
        val group = assertIs<OutputResourceGroup>(resource)
        assertEquals(2, group.resources.size)
    }
}
