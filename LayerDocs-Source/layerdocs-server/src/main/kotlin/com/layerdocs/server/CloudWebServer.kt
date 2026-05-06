package com.layerdocs.server

import com.layerdocs.server.endpoints.CompileEndpoint
import com.layerdocs.server.stop.KtorStoppableAdapter
import com.layerdocs.server.stop.Stoppable
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

/**
 * A server specifically designed for cloud rendering.
 * It provides a /compile endpoint to render LayerDocs source code remotely.
 */
class CloudWebServer(
    private val pipelineFactory: () -> com.layerdocs.core.pipeline.Pipeline
) : Server {
    private val compile = CompileEndpoint(pipelineFactory)

    override fun start(port: Int, wait: Boolean, onReady: (Stoppable) -> Unit) {
        embeddedServer(Netty, port, host = "0.0.0.0") {
            monitor.subscribe(ServerReady) { onReady(KtorStoppableAdapter(this)) }

            routing {
                get("/") {
                    call.respondText("LayerDocs Cloud Renderer is active and ready to process requests.")
                }
                
                post(ServerEndpoints.COMPILE) {
                    compile.handleRequest(call)
                }
            }
        }.start(wait = wait)
    }
}
