package com.layerdocs.rendering.html.post.resources

import com.layerdocs.core.media.storage.ReadOnlyMediaStorage
import com.layerdocs.core.pipeline.output.OutputResource

/**
 * A [PostRendererResource] that includes media (such as images)
 * from the document's media storage into the output, in its own directory.
 *
 * @param mediaStorage the storage containing media files referenced by the document
 */
class MediaPostRendererResource(
    private val mediaStorage: ReadOnlyMediaStorage,
) : PostRendererResource {
    override fun includeTo(
        resources: MutableSet<OutputResource>,
        rendered: CharSequence,
    ) {
        if (!mediaStorage.isEmpty) {
            resources += mediaStorage.toResource()
        }
    }
}
