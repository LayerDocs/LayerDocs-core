package com.layerdocs.rendering.html.post.resources

import com.layerdocs.core.pipeline.output.ArtifactType
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.output.TextOutputArtifact
import com.layerdocs.rendering.html.search.SearchIndex
import kotlinx.serialization.json.Json

/**
 * A [PostRendererResource] that outputs a search index as a JSON file.
 *
 * The generated `search-index.json` file is placed in the output directory
 * and can be fetched by client-side JavaScript to provide documentation search
 * without requiring a server.
 *
 * @param index the search index to serialize
 * @see com.layerdocs.rendering.html.search.SearchIndex
 * @see com.layerdocs.rendering.html.search.SearchIndexGenerator
 */
class SearchIndexPostRendererResource(
    private val index: SearchIndex,
) : PostRendererResource {
    override fun includeTo(
        resources: MutableSet<OutputResource>,
        rendered: CharSequence,
    ) {
        resources +=
            TextOutputArtifact(
                name = "search-index",
                content = Json.encodeToString(index),
                type = ArtifactType.JSON,
            )
    }
}
