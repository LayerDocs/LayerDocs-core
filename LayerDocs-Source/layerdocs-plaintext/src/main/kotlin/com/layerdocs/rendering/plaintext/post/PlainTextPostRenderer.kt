package com.layerdocs.rendering.plaintext.post

import com.layerdocs.core.context.Context
import com.layerdocs.core.document.sub.getOutputFileName
import com.layerdocs.core.media.storage.options.MediaStorageOptions
import com.layerdocs.core.media.storage.options.ReadOnlyMediaStorageOptions
import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.output.OutputResourceGroup
import com.layerdocs.core.pipeline.output.TextOutputArtifact
import com.layerdocs.core.pipeline.output.visitor.copy
import com.layerdocs.core.rendering.PostRenderer

/**
 * Post-renderer that generates plain-text output artifacts.
 *
 * - Produces a single plain-text file if there is only one subdocument.
 * - Produces a resource group of plain-text files if there are multiple subdocuments.
 */
class PlainTextPostRenderer(
    private val context: Context,
) : PostRenderer {
    override val preferredMediaStorageOptions: MediaStorageOptions
        get() = ReadOnlyMediaStorageOptions()

    override fun wrap(content: CharSequence): CharSequence = content

    override fun generateResources(rendered: CharSequence): Set<OutputResource> =
        setOf(
            TextOutputArtifact(
                name = context.subdocument.getOutputFileName(context),
                content = rendered.trimEnd(),
                type = ArtifactType.PLAIN_TEXT,
            ),
        )

    override fun wrapResources(
        name: String,
        resources: Set<OutputResource>,
    ): OutputResource {
        // Single output file.
        resources.singleOrNull()?.let {
            return it.copy(name = name)
        }
        // Multiple output files.
        return OutputResourceGroup(
            name = name,
            resources = resources,
        )
    }
}
