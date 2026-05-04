package com.layerdocs.cli.exec.strategy

import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.output.OutputResource

/**
 * A strategy to execute a [Pipeline].
 */
interface PipelineExecutionStrategy {
    /**
     * Executes the [pipeline].
     * @param pipeline pipeline to execute
     */
    fun execute(pipeline: Pipeline): OutputResource?
}
