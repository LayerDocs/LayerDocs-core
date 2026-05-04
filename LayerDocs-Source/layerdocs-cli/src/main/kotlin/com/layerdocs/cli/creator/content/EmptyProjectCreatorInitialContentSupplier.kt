package com.layerdocs.cli.creator.content

import com.layerdocs.core.pipeline.output.OutputResource

/**
 * A [ProjectCreatorInitialContentSupplier] that provides no initial content or resources.
 */
class EmptyProjectCreatorInitialContentSupplier : ProjectCreatorInitialContentSupplier {
    override val templateCodeContent: String? = null

    override fun createResources(): Set<OutputResource> = emptySet()
}
