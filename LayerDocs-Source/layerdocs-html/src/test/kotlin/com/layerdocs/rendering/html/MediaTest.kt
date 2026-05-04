package com.layerdocs.rendering.html

import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.media.StoredMediaProperty
import com.layerdocs.core.attachMockPipeline
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.media.storage.MEDIA_SUBDIRECTORY_NAME
import com.layerdocs.core.media.storage.StoredMedia
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.rendering.html.node.LayerDocsHtmlNodeRenderer
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val WORKING_DIR_PATH = "src/test/resources"
private const val REMOTE_URL = "https://SatyamPote.eu/layerdocs/img/logo-light.svg"
private const val REMOTE_OUT_NAME = "https-SatyamPote.eu-layerdocs-img-logo-light.svg"
private const val LOCAL_PATH = "media/icon.png"
private const val OUT_DIR = MEDIA_SUBDIRECTORY_NAME

/**
 * Tests for media resolution and rendering via the HTML renderer.
 */
class MediaTest {
    private lateinit var context: MutableContext
    private lateinit var renderer: LayerDocsHtmlNodeRenderer

    @BeforeTest
    fun setUp() {
        context = MutableContext(LayerDocsFlavor)
        renderer = LayerDocsHtmlNodeRenderer(context)
        context.options.enableLocalMediaStorage = true
        context.options.enableRemoteMediaStorage = true
        context.attachMockPipeline(
            PipelineOptions(
                permissions =
                    setOf(
                        Permission.ProjectRead,
                        Permission.GlobalRead,
                        Permission.NetworkAccess,
                    ),
            ),
        )
    }

    private fun Node.attach(media: StoredMedia?) {
        if (media == null) return
        context.attributes.of(this) += StoredMediaProperty(media)
    }

    private fun remoteImage(media: StoredMedia?) =
        Image(
            Link(
                label = listOf(),
                url = REMOTE_URL,
                title = null,
            ).apply { attach(media) },
            width = null,
            height = null,
        )

    private fun localImage(media: StoredMedia?) =
        Image(
            Link(
                label = listOf(),
                url = LOCAL_PATH,
                title = null,
            ).apply { attach(media) },
            width = null,
            height = null,
        )

    @Test
    fun `remote media path update`() {
        val media =
            context.mediaStorage.register(REMOTE_URL, workingDirectory = null)!!

        val image = remoteImage(media)

        assertEquals(
            "<img src=\"$OUT_DIR/$REMOTE_OUT_NAME\" alt=\"\" />",
            image.accept(renderer),
        )
    }

    @Test
    fun `local media path update`() {
        val media = context.mediaStorage.register(LOCAL_PATH, workingDirectory = File(WORKING_DIR_PATH))!!
        val image = localImage(media)

        assertTrue(image.accept(renderer).startsWith("<img src=\"$OUT_DIR/icon@"))
    }
}
