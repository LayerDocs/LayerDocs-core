package com.layerdocs.test.util

import com.layerdocs.core.context.Context
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.options.MutableContextOptions
import com.layerdocs.core.context.subdocument.subdocumentGraph
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.document.sub.SubdocumentOutputNaming
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.graph.VisitableOnceGraph
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.PipelineHooks
import com.layerdocs.core.pipeline.PipelineOptions
import com.layerdocs.core.pipeline.error.PipelineErrorHandler
import com.layerdocs.core.pipeline.error.StrictPipelineErrorHandler
import com.layerdocs.core.pipeline.output.OutputResource
import com.layerdocs.core.rendering.RenderingComponents
import com.layerdocs.rendering.html.extension.html
import com.layerdocs.stdlib.Stdlib
import java.io.File

// Folder to retrieve test data from.
const val DATA_FOLDER = "src/test/resources/data"

// Folder to retrieve 'dummy' libraries from, relative to the data folder.
private const val LOCAL_LIBRARY_DIRECTORY = "libraries"

// Folder to retrieve actual libraries from.
private const val GLOBAL_LIBRARY_DIRECTORY = "../layerdocs-libs/src/main/resources"

// Default execution options.
val DEFAULT_OPTIONS =
    MutableContextOptions(
        enableAutomaticIdentifiers = false,
        enableLocationAwareness = false,
    )

/**
 * Executes a LayerDocs source.
 * @param source LayerDocs source to execute
 * @param options execution options
 * @param renderer function that provides the rendering components to use (defaults to HTML)
 * @param workingDirectory working directory to use for the execution, used for resolving relative paths and as the root for the file system
 * @param subdocumentGraph modifier of the subdocument graph before rendering
 * @param loadableLibraries file names to export as libraries from the `data/libraries` folder, and loadable by the user via `.include`
 * @param useDummyLibraryDirectory whether to use the dummy library directory for loading libraries instead of the one from the `libs` module
 * @param errorHandler error handler to use
 * @param enableMediaStorage whether the media storage system should be enabled.
 * If enabled, nodes that reference media (e.g. images) will instead reference the path to the media on the local storage
 * @param permissions the set of permissions granted to the pipeline
 * @param subdocumentNaming the strategy used to determine subdocument output file names
 * @param previewMode whether to simulate preview mode, which suppresses certain resources from being generated
 * @param outputResourceHook action run after the pipeline execution, with the output resource as a parameter
 * @param afterPostRenderingHook action run after post-rendering. Parameters are the pipeline context and the post-rendered result
 * @param afterRenderingHook action run after rendering. Parameters are the pipeline context and the rendered result
 */
fun execute(
    source: String,
    options: MutableContextOptions = DEFAULT_OPTIONS.copy(),
    renderer: (RendererFactory, Context) -> RenderingComponents = { rendererFactory, ctx -> rendererFactory.html(ctx) },
    workingDirectory: File = File(DATA_FOLDER),
    subdocumentGraph: (VisitableOnceGraph<Subdocument>) -> VisitableOnceGraph<Subdocument> = { it },
    loadableLibraries: Set<String> = emptySet(),
    useDummyLibraryDirectory: Boolean = false,
    errorHandler: PipelineErrorHandler = StrictPipelineErrorHandler(),
    enableMediaStorage: Boolean = false,
    permissions: Set<Permission> = Permission.DEFAULT_SET,
    subdocumentNaming: SubdocumentOutputNaming = SubdocumentOutputNaming.FILE_NAME,
    previewMode: Boolean = false,
    outputResourceHook: Context.(OutputResource?) -> Unit = {},
    afterPostRenderingHook: Context.(CharSequence) -> Unit = {},
    afterRenderingHook: Context.(CharSequence) -> Unit,
) {
    val context =
        MutableContext(
            LayerDocsFlavor,
            options = options,
            loadableLibraries =
                LibraryUtils.export(
                    loadableLibraries,
                    if (useDummyLibraryDirectory) {
                        File(DATA_FOLDER, LOCAL_LIBRARY_DIRECTORY)
                    } else {
                        File(GLOBAL_LIBRARY_DIRECTORY)
                    },
                ),
        )

    val hooks =
        PipelineHooks(
            afterTreeTraversal = {
                context.subdocumentGraph = subdocumentGraph(context.subdocumentGraph)
            },
            afterRendering = {
                afterRenderingHook(readOnlyContext, it)
            },
            afterPostRendering = {
                afterPostRenderingHook(readOnlyContext, it)
            },
        )

    val pipeline =
        Pipeline(
            context,
            PipelineOptions(
                errorHandler = errorHandler,
                workingDirectory = workingDirectory,
                enableMediaStorage = enableMediaStorage,
                permissions = permissions,
                subdocumentNaming = subdocumentNaming,
                isPreview = previewMode,
            ),
            libraries = setOf(Stdlib.library),
            renderer = renderer,
            hooks,
        )

    val resource = pipeline.execute(source)
    outputResourceHook(context, resource)
}
