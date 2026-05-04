package com.layerdocs.cli.exec.strategy

import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.output.OutputResource
import java.io.File

/**
 * A strategy to execute a [Pipeline] from the string content of a file.
 */
class FileExecutionStrategy(
    private val file: File,
) : PipelineExecutionStrategy {
    override fun execute(pipeline: Pipeline): OutputResource? = pipeline.execute(file.readText())
}
