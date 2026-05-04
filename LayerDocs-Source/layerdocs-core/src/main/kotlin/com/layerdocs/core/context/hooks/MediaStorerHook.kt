package com.layerdocs.core.context.hooks

import com.layerdocs.core.ast.attributes.error.setError
import com.layerdocs.core.ast.attributes.link.getResolvedUrl
import com.layerdocs.core.ast.base.LinkNode
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.base.inline.ReferenceImage
import com.layerdocs.core.ast.iterator.AstIteratorHook
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.ast.media.StoredMediaProperty
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.log.Log
import com.layerdocs.core.media.passthrough.MediaPassthrough
import com.layerdocs.core.media.storage.StoredMedia
import com.layerdocs.core.permissions.MissingPermissionException

/**
 * Hook that, when a node containing information about media is found,
 * registers it in the media storage of [MutableContext.mediaStorage].
 * A media storage is a temporary lookup table that maps media to their paths, so that they can be resolved later.
 * @param context the context containing the media storage to register media into
 */
class MediaStorerHook(
    private val context: MutableContext,
) : AstIteratorHook {
    /**
     * Registers a media contained within a link into the media storage
     * and attaches the new media to the node's extra attributes.
     *
     * [getResolvedUrl] is used rather than [LinkNode.url] in case a different URL was set by [LinkUrlResolverHook].
     *
     * @param link the link node containing the media to register.
     * It is also the node to attach the [StoredMediaProperty] to, into [com.layerdocs.core.ast.attributes.AstAttributes.properties]
     */
    private fun register(link: LinkNode) {
        val url = link.getResolvedUrl(context)
        if (MediaPassthrough.isPassthroughPath(url)) {
            Log.debug("Media is a passthrough: ${link.url}")
            return
        }

        val media: StoredMedia? =
            try {
                context.mediaStorage.register(
                    url,
                    context.fileSystem.workingDirectory,
                )
            } catch (_: IllegalArgumentException) {
                Log.warn("Media cannot be resolved: ${link.url}")
                return
            } catch (e: MissingPermissionException) {
                link.setError(e, context)
                return
            }

        // The stored media is attached to the node's extra attributes.
        media
            ?.let(::StoredMediaProperty)
            ?.also { Log.debug("Registered media: ${link.url} -> ${it.value}") }
            ?.let {
                context.attributes.of(link) += it
            }
    }

    override fun attach(iterator: ObservableAstIterator) {
        // Images are instantly registered.
        iterator.on<Image> {
            if (it.usesMediaStorage) {
                register(it.link)
            }
        }

        // Reference images are registered upon resolution,
        // i.e. when a definition that matches the reference is found.
        iterator.on<ReferenceImage> { it.link.onResolve.add(::register) }
    }
}
