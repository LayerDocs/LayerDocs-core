package com.layerdocs.cli.exec

import com.layerdocs.cli.CliOptions
import com.layerdocs.cli.PipelineInitialization
import com.layerdocs.cli.exec.strategy.PipelineExecutionStrategy
import com.layerdocs.cli.lib.QdLibraries
import com.layerdocs.cli.server.WebServerOptions
import com.layerdocs.cli.server.WebServerStarter
import com.layerdocs.cli.util.cleanDirectory
import com.layerdocs.core.flavor.MarkdownFlavor
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.function.error.FunctionCallRuntimeException
import com.layerdocs.core.function.library.LibraryExporter
import com.layerdocs.core.log.Log
import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.core.pipeline.error.PipelineException
import com.layerdocs.core.pipeline.output.visitor.saveTo
import com.layerdocs.server.message.ServerMessage
import com.layerdocs.server.message.ServerMessageSession
import java.io.IOException
import kotlin.system.exitProcess

/**
 * Executes a complete LayerDocs pipeline.
 * @param executionStrategy launch strategy of the pipeline, e.g. from file or REPL
 * @param cliOptions options that define the behavior of the CLI, especially I/O
 * @param pipelineOptions options that define the behavior of the pipeline
 * @return the output file or directory, if any, associated with the executed pipeline
 */
fun runLayerDocs(
    executionStrategy: PipelineExecutionStrategy,
    cliOptions: CliOptions,
    pipelineOptions: PipelineOptions,
): ExecutionOutcome {
    // Flavor to use across the pipeline.
    val flavor: MarkdownFlavor = LayerDocsFlavor

    // External libraries loaded from .qd files.
    val libraries: Set<LibraryExporter> =
        try {
            cliOptions.libraryDirectory?.let(QdLibraries::fromDirectory) ?: emptySet()
        } catch (e: Exception) {
            Log.warn(e.message ?: "")
            emptySet()
        }

    // The pipeline that contains all the stages to go through,
    // from the source input to the final output.
    val pipeline: Pipeline =
        PipelineInitialization.init(
            flavor,
            libraries,
            pipelineOptions,
            printOutput = cliOptions.pipe,
            cliOptions.renderer,
        )

    // Output directory to save the generated resources in.
    val outputDirectory = cliOptions.outputDirectory

    try {
        // Cleans the output directory if enabled in options.
        if (cliOptions.clean) {
            outputDirectory?.cleanDirectory()
        }

        // Pipeline execution and output resource retrieving.
        val resource = executionStrategy.execute(pipeline)
        // Exports the generated resources to file if enabled in options.
        val childDirectory = outputDirectory?.let { resource?.saveTo(it) }

        return ExecutionOutcome(resource, childDirectory, pipeline)
    } catch (e: PipelineException) {
        val targetException = (e as? FunctionCallRuntimeException)?.cause ?: e
        targetException.printStackTrace()
        exitProcess(e.code)
    }
}

/**
 * Communicates with the server to reload the requested resources.
 * If the session is not active, starts the server.
 * @param options information to start the web server
 * @param session the session to communicate with the server to handle preview reloads
 */
fun runServerCommunication(
    options: WebServerOptions,
    session: ServerMessageSession,
) {
    if (!session.isConnected) {
        Log.info("Starting server...")
        WebServerStarter.start(options, session, onSessionReady = {
            runServerCommunication(options, session)
        })
        return
    }

    // Sends a reload message to the server.
    try {
        ServerMessage().send(session)
        return
    } catch (e: IOException) {
        Log.error("Could not communicate with the server on port ${options.port}: ${e.message}")
        Log.debug(e)
    }
}
