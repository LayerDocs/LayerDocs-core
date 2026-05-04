package com.layerdocs.core.media.storage.permissions

import com.layerdocs.core.media.LocalMedia
import com.layerdocs.core.media.MediaVisitor
import com.layerdocs.core.media.RemoteMedia
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.permissions.PermissionHolder
import com.layerdocs.core.permissions.requirePermission
import com.layerdocs.core.permissions.requireReadPermission

/**
 * Checks whether a media type is allowed to be stored according to the granted permissions in [context].
 * If a required permission is missing, a [com.layerdocs.core.permissions.MissingPermissionException] will be thrown.
 * @param context the context to access the media storage from
 */
class MediaAccessPermissionChecker(
    private val holder: PermissionHolder,
) : MediaVisitor<Unit> {
    override fun visit(media: LocalMedia) {
        holder.requireReadPermission(media.file)
    }

    override fun visit(media: RemoteMedia) {
        holder.requirePermission(
            Permission.NetworkAccess,
            message = "Cannot access remote media ${media.url}",
        )
    }
}
