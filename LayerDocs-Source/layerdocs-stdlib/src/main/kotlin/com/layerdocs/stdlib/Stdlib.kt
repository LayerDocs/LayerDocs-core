package com.layerdocs.stdlib

import com.layerdocs.core.function.library.Library
import com.layerdocs.core.function.library.LibraryExporter
import com.layerdocs.core.function.library.loader.MultiFunctionLibraryLoader
import com.layerdocs.core.function.value.NoneValue
import com.layerdocs.core.function.value.OutputValue
import com.layerdocs.core.pipeline.PipelineHooks

/**
 * Fallback value for non-existent elements in collections, dictionaries, and more.
 */
val NOT_FOUND: OutputValue<*>
    get() = NoneValue

/**
 * Exporter of LayerDocs's standard library.
 */
object Stdlib : LibraryExporter {
    override val library: Library
        get() =
            MultiFunctionLibraryLoader(name = "stdlib")
                .load(
                    Document,
                    Layout,
                    Text,
                    Primitives,
                    MiscElements,
                    Math,
                    Logical,
                    String,
                    Icon,
                    Emoji,
                    Collection,
                    Dictionary,
                    Optionality,
                    Logger,
                    Flow,
                    TableComputation,
                    Data,
                    Localization,
                    Library,
                    Slides,
                    Ecosystem,
                    Html,
                    Mermaid,
                    Reference,
                    Bibliography,
                ).withHooks(
                    PipelineHooks(
                        // Localization data is loaded before any function is called.
                        afterRegisteringLibraries = {
                            includeResource(
                                this.readOnlyContext,
                                javaClass.getResourceAsStream("/lib/localization.qd")!!.reader(),
                            )
                        },
                    ),
                )
}
