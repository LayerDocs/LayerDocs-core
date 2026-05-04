package com.layerdocs.core.function.library.module

import com.layerdocs.core.function.library.loader.ExportableFunction
import com.layerdocs.core.function.library.loader.MultiFunctionLibraryLoader

/**
 * A subsection of LayerDocs functions that can be exported via a [MultiFunctionLibraryLoader].
 *
 * While this class might seem redundant in place of a typealias,
 * having an actual class makes it easier and more robust on LayerDoc[^1]'s side to identify modules.
 *
 * [^1]: LayerDoc is LayerDocs's documentation generator, based on Dokka. See the `layerdocs-layerdoc` module.
 *
 * @param functions the functions to export in the module
 */
class LayerDocsModule(
    functions: Set<ExportableFunction>,
) : HashSet<ExportableFunction>(functions) {
    /**
     * Creates a [LayerDocsModule] that wraps multiple [LayerDocsModule]s, joining their functions into a single module.
     * The identity of the submodules is lost in the process.
     * @param modules the modules to include
     */
    constructor(vararg modules: LayerDocsModule) : this(modules.flatMap { it.asSequence() }.toSet())

    operator fun plus(other: LayerDocsModule): LayerDocsModule = LayerDocsModule(this + other)
}

/**
 * Creates a [LayerDocsModule] from a set of Kotlin functions.
 * @param functions the functions to export in the module
 */
fun moduleOf(vararg functions: ExportableFunction): LayerDocsModule = setOf(*functions).let(::LayerDocsModule)
