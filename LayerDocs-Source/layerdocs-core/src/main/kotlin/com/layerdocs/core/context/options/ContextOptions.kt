package com.layerdocs.core.context.options

import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.context.Context
import com.layerdocs.core.media.storage.options.MediaStorageOptions

/**
 * Read-only properties that affect several behaviors of the document generation process,
 * and that can be altered through function calls through its [MutableContextOptions] implementation.
 */
interface ContextOptions : MediaStorageOptions {
    /**
     * When a [Heading] node has a depth equals or less than this value, a page break is forced.
     */
    val autoPageBreakHeadingMaxDepth: Int

    /**
     * Whether automatic identifiers should be generated for elements
     * that do not have an explicit one.
     * For example, a heading element (`# Hello world`) automatically generates
     * an identifier (`hello-world`) that can be referenced by other elements.
     * @see com.layerdocs.core.ast.attributes.id.IdentifierProvider
     */
    val enableAutomaticIdentifiers: Boolean

    /**
     * Whether certain nodes can be aware of their location within the document
     * in order to display it, for example in headings.
     * @see com.layerdocs.core.ast.attributes.location.LocationTrackableNode
     */
    val enableLocationAwareness: Boolean

    /**
     * The suffixes that, if matched by a link's URL, indicates that the link points to a LayerDocs subdocument.
     * @see com.layerdocs.core.ast.base.inline.SubdocumentLink
     */
    val subdocumentUrlSuffixes: Set<String>

    /**
     * Supplier of unique identifiers (UUIDs). For instance, UUIDs are generated for anonymous footnotes.
     */
    val uuidSupplier: () -> String

    /**
     * Native HTML generation options.
     */
    val html: HtmlOptions
}

/**
 * @return whether the [heading] node should force a page break
 * @see ContextOptions.autoPageBreakHeadingMaxDepth
 */
fun Context.shouldAutoPageBreak(heading: Heading) =
    heading.canBreakPage &&
        heading.depth <= this.options.autoPageBreakHeadingMaxDepth

/**
 * @param url URL or file path to check, without any surrounding whitespace or anchors
 * @return whether the given [url], which may also be a file path, points to a LayerDocs subdocument
 * depending on its suffix (file extension).
 * @see com.layerdocs.core.ast.base.inline.SubdocumentLink
 */
fun Context.isSubdocumentUrl(url: String): Boolean = options.subdocumentUrlSuffixes.any { url.endsWith(it, ignoreCase = true) }
