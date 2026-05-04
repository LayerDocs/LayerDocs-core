package com.layerdocs.core.media.export

import com.layerdocs.core.media.LocalMedia
import com.layerdocs.core.media.Media
import com.layerdocs.core.media.MediaVisitor
import com.layerdocs.core.media.RemoteMedia
import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.BinaryOutputArtifact
import com.layerdocs.core.pipeline.output.FileReferenceOutputArtifact
import com.layerdocs.core.pipeline.output.OutputResource

/**
 * A converter of a [Media] to an [OutputResource].
 * @param name generated media name
 */
class MediaOutputResourceConverter(
    private val name: String,
) : MediaVisitor<OutputResource> {
    override fun visit(media: LocalMedia) =
        FileReferenceOutputArtifact(
            name,
            media.file,
            useChecksumInvalidation = true,
        )

    override fun visit(media: RemoteMedia) =
        BinaryOutputArtifact(
            name,
            media.url
                .openStream()
                .readBytes()
                .toList(),
            ArtifactType.AUTO,
        )
}
