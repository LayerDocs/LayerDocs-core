package com.layerdocs.core.context.hooks

import com.layerdocs.core.RUNTIME_ERROR_EXIT_CODE
import com.layerdocs.core.ast.attributes.error.setError
import com.layerdocs.core.ast.base.inline.SubdocumentLink
import com.layerdocs.core.ast.base.inline.setSubdocument
import com.layerdocs.core.ast.iterator.AstIteratorHook
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.subdocument.findResourceByPath
import com.layerdocs.core.context.subdocument.subdocumentGraph
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.permissions.MissingPermissionException
import com.layerdocs.core.permissions.requireReadPermission
import com.layerdocs.core.pipeline.error.PipelineException

/**
 * Hook that registers [Subdocument]s in the subdocument graph of [context].
 * A subdocument is a separate document file that is referenced by a link from the main document or another subdocument.
 * @param context the context to attach this hook to
 */
class SubdocumentRegistrationHook(
    private val context: MutableContext,
) : AstIteratorHook {
    override fun attach(iterator: ObservableAstIterator) {
        iterator.on<SubdocumentLink> { link ->
            val fileSystem = link.fileSystem ?: context.fileSystem
            val file = fileSystem.resolve(path = link.url)

            if (!file.exists()) {
                link.setError(UnresolvedSubdocumentException(link), context)
                return@on
            }

            try {
                context.requireReadPermission(file)
            } catch (e: MissingPermissionException) {
                link.setError(e, context)
                return@on
            }

            val path = file.canonicalFile.absolutePath

            // Reuse an already-registered subdocument to avoid redundant file I/O.
            val subdocument =
                context.subdocumentGraph.findResourceByPath(path)
                    ?: Subdocument.Resource(
                        name = file.nameWithoutExtension,
                        path = path,
                        workingDirectory = file.parentFile.canonicalFile,
                        content = file.readText(),
                    )

            link.setSubdocument(context, subdocument)

            context.subdocumentGraph =
                context.subdocumentGraph
                    .addVertexAndEdge(
                        vertex = subdocument,
                        edgeFrom = context.subdocument,
                        edgeTo = subdocument,
                    )
        }
    }
}

/**
 * Exception thrown when a [SubdocumentLink] cannot be resolved to an existing resource.
 * @param link the link that failed to resolve
 */
class UnresolvedSubdocumentException(
    link: SubdocumentLink,
) : PipelineException(
        message = "Cannot resolve subdocument link: ${link.url}",
        code = RUNTIME_ERROR_EXIT_CODE,
    )
