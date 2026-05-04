package com.layerdocs.layerdoc.reader

/**
 * Extractor of content of a documentation resource.
 * @see com.layerdocs.layerdoc.reader.dokka.DokkaHtmlContentExtractor
 */
interface DocsContentExtractor {
    /**
     * @return the extracted main content, if available
     */
    fun extractContent(): String?

    /**
     * @return the function data that this documentation resource describes, if it is about a function
     */
    fun extractFunctionData(): DocsFunction?
}
