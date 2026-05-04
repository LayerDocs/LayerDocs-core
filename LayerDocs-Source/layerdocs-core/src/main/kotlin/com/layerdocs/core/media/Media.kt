package com.layerdocs.core.media

/**
 * Represents a resource that can be referenced in a LayerDocs document
 * and that may need to be downloaded or processed.
 * For example, when exporting a document to HTML, remote images are handled by the browser,
 * while local ones need to be copied to the output resources.
 */
sealed interface Media {
    fun <T> accept(visitor: MediaVisitor<T>): T
}
