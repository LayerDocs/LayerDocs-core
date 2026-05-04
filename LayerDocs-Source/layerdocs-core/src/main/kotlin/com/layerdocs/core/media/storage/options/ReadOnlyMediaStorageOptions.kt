package com.layerdocs.core.media.storage.options

/**
 * Read-only options that affect the rules of a media storage.
 * Used in [com.layerdocs.core.rendering.PostRenderer] to determine the preferred rules of a rendering strategy.
 */
data class ReadOnlyMediaStorageOptions(
    override val enableRemoteMediaStorage: Boolean? = null,
    override val enableLocalMediaStorage: Boolean? = null,
) : MediaStorageOptions
