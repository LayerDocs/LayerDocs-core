package com.layerdocs.core.function.library.loader

import com.layerdocs.core.function.library.Library
import com.layerdocs.core.function.library.module.LayerDocsModule

/**
 * Creates a library from a set of Kotlin functions exported in a [LayerDocsModule].
 * @param name name to assign to the library
 * @see FunctionLibraryLoader
 */
class MultiFunctionLibraryLoader(
    private val name: String,
) : LibraryLoader<LayerDocsModule> {
    override fun load(source: LayerDocsModule): Library =
        MultiLibraryLoader(this.name, FunctionLibraryLoader())
            .load(source)

    /**
     * Creates a library from a set of Kotlin functions exported in multiple [LayerDocsModule]s.
     */
    fun load(vararg sources: LayerDocsModule): Library = load(LayerDocsModule(*sources))
}
