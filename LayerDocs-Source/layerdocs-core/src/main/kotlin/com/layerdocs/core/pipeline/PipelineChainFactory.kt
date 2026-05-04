package com.layerdocs.core.pipeline

import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.pipeline.stage.PipelineStage
import com.layerdocs.core.pipeline.stage.then
import com.layerdocs.core.pipeline.stage.thenOptionally
import com.layerdocs.core.pipeline.stages.AfterAllRenderingPeek
import com.layerdocs.core.pipeline.stages.AttachmentStage
import com.layerdocs.core.pipeline.stages.AttributesUpdateStage
import com.layerdocs.core.pipeline.stages.FunctionCallExpansionStage
import com.layerdocs.core.pipeline.stages.LexingStage
import com.layerdocs.core.pipeline.stages.LibrariesRegistrationStage
import com.layerdocs.core.pipeline.stages.ParsingStage
import com.layerdocs.core.pipeline.stages.PostRenderingStage
import com.layerdocs.core.pipeline.stages.RenderingStage
import com.layerdocs.core.pipeline.stages.ResourceGenerationStage
import com.layerdocs.core.pipeline.stages.TreeTraversalStage
import com.layerdocs.core.rendering.RenderingComponents

/**
 * Factory for creating standard pipeline stage chains.
 */
object PipelineChainFactory {
    /**
     * Creates a full pipeline stage chain that processes the input source text
     * through all stages, up to resource generation.
     *
     * @param source the raw input text to be processed
     * @param renderingComponents the rendering components to use in the rendering stages
     * @return a pipeline stage that processes the input source text and produces output resources
     */
    fun fullChain(
        source: CharSequence,
        renderingComponents: RenderingComponents,
        options: PipelineOptions,
    ): PipelineStage<Unit, Set<OutputResource>> =
        AttachmentStage then
            LibrariesRegistrationStage then
            LexingStage(source) then
            ParsingStage then
            AttributesUpdateStage(preferredMediaStorageOptions = renderingComponents.postRenderer.preferredMediaStorageOptions) then
            FunctionCallExpansionStage then
            TreeTraversalStage then
            RenderingStage(renderingComponents.nodeRenderer) thenOptionally
            PostRenderingStage(renderingComponents.postRenderer).takeIf { options.wrapOutput } then
            AfterAllRenderingPeek then
            ResourceGenerationStage(renderingComponents.postRenderer)
}
