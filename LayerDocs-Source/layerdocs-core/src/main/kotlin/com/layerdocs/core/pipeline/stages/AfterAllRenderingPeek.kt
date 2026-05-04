package com.layerdocs.core.pipeline.stages

import com.layerdocs.core.pipeline.PipelineHooks
import com.layerdocs.core.pipeline.stage.PeekPipelineStage
import com.layerdocs.core.pipeline.stage.SharedPipelineData

/**
 * Peek stage that runs after all rendering has been completed.
 *
 * - It usually matches with [PipelineHooks.afterPostRendering].
 * - If post-rendering is disabled by [com.layerdocs.core.pipeline.PipelineOptions.wrapOutput] set to `false`,
 *   it will match with [PipelineHooks.afterRendering] instead.
 *
 * This stage allows for consistently hooking into the final rendered output,
 * regardless of whether post-rendering is enabled or not.
 */
object AfterAllRenderingPeek : PeekPipelineStage<CharSequence> {
    override val hook = PipelineHooks::afterAllRendering

    override fun peek(
        input: CharSequence,
        data: SharedPipelineData,
    ) {}
}
