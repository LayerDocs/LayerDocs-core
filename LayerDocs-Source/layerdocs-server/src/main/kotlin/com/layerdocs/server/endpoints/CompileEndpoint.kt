package com.layerdocs.server.endpoints

import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.output.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText

/**
 * Handler for the cloud compilation endpoint (`/compile`) which takes LayerDocs source code
 * and returns the rendered PDF as a byte stream.
 */
class CompileEndpoint(
    private val pipelineFactory: () -> Pipeline
) {
    suspend fun handleRequest(call: ApplicationCall) {
        val source = call.receiveText()
        if (source.isEmpty()) {
            call.respondText("Empty source provided.", status = HttpStatusCode.BadRequest)
            return
        }

        try {
            val pipeline = pipelineFactory()
            val result = pipeline.execute(source)

            if (result != null) {
                val bytes = result.accept(PdfByteVisitor)
                if (bytes != null) {
                    call.respondBytes(bytes, ContentType.Application.Pdf)
                } else {
                    call.respondText("No PDF artifact was produced.", status = HttpStatusCode.InternalServerError)
                }
            } else {
                call.respondText("Rendering failed to produce a result.", status = HttpStatusCode.InternalServerError)
            }
        } catch (e: Exception) {
            call.respondText("Compilation error: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    private object PdfByteVisitor : OutputResourceVisitor<ByteArray?> {
        override fun visit(artifact: TextOutputArtifact): ByteArray? = null
        
        override fun visit(artifact: BinaryOutputArtifact): ByteArray {
            return artifact.content.toByteArray()
        }

        override fun visit(group: OutputResourceGroup): ByteArray? {
            // Find the first binary artifact (usually the final PDF)
            return group.resources.firstNotNullOfOrNull { it.accept(this) }
        }

        override fun visit(artifact: FileReferenceOutputArtifact): ByteArray? = null
    }
}
