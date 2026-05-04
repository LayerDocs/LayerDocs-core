package com.layerdocs.cli.creator.content

import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.LazyOutputArtifact
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.output.OutputResourceGroup

private const val RESOURCES_PATH = "/creator/"
private const val CODE_CONTENT_PATH = RESOURCES_PATH + "initialcontent.qd.jte"
private const val LOGO = "logo.png"
private const val IMAGES_GROUP_NAME = "image"

/**
 * A [ProjectCreatorInitialContentSupplier] that provides some initial content for introduction purposes:
 * - A simple LayerDocs code snippet
 * - An image of the LayerDocs logo
 */
class DefaultProjectCreatorInitialContentSupplier : ProjectCreatorInitialContentSupplier {
    override val templateCodeContent: String
        get() = javaClass.getResourceAsStream(CODE_CONTENT_PATH)!!.bufferedReader().readText()

    private val imageGroup: OutputResource
        get() =
            OutputResourceGroup(
                IMAGES_GROUP_NAME,
                setOf(
                    LazyOutputArtifact.internal(
                        RESOURCES_PATH + LOGO,
                        LOGO,
                        ArtifactType.AUTO,
                    ),
                ),
            )

    override fun createResources(): Set<OutputResource> = setOf(imageGroup)
}
