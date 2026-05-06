package com.layerdocs.cli.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.layerdocs.cli.CliOptions
import com.layerdocs.cli.PipelineInitialization
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.server.CloudWebServer
import com.layerdocs.server.Server

/**
 * Command to start a cloud rendering server.
 * This server allows remote clients (like Google Colab) to send LayerDocs code
 * and receive a rendered PDF, bypassing local RAM limits.
 */
class CloudServerCommand : CliktCommand(name = "cloud") {
    /**
     * Port to start the server on.
     */
    private val port: Int by option("-p", "--port", help = "Port to start the server on")
        .int()
        .default(DEFAULT_SERVER_PORT)

    override fun run() {
        val server: Server = CloudWebServer {
            // Configure a standard PDF pipeline for cloud rendering
            val cliOptions = CliOptions(
                source = null,
                outputDirectory = null,
                libraryDirectory = null,
                rendererName = "html",
                clean = false,
                pipe = false,
                nodePath = "node",
                npmPath = "npm",
                exportPdf = true,
                combine = true
            )
            
            val pipelineOptions = PipelineOptions(
                resourceName = "CloudRender",
                prettyOutput = false,
                wrapOutput = true
            )

            PipelineInitialization.init(
                LayerDocsFlavor,
                emptySet(),
                pipelineOptions,
                printOutput = false,
                renderer = com.layerdocs.cli.renderer.RendererRetriever(cliOptions).getRenderer()
            )
        }

        println("--------------------------------------------------")
        println("🚀 LayerDocs Cloud Renderer is starting...")
        println("📡 Listening on: http://0.0.0.0:$port")
        println("📝 Endpoint: POST /compile")
        println("--------------------------------------------------")
        
        server.start(port, wait = true)
    }
}
