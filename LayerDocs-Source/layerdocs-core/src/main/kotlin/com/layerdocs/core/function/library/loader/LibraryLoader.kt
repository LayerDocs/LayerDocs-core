package com.layerdocs.core.function.library.loader

import com.layerdocs.core.function.library.Library

/**
 * Loads libraries from a generic source.
 * @param S type of source to extract a library from
 * @see com.layerdocs.core.function.reflect.loaders
 */
interface LibraryLoader<S> {
    /**
     * Loads a library from a source.
     * @param source source to extract the library from
     * @return the extracted library
     */
    fun load(source: S): Library
}
