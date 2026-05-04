package com.layerdocs.stdlib.internal

import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.misc.font.FontFamily
import com.layerdocs.core.misc.font.resolver.FontFamilyResolver

/**
 * Resolves a font family by its name or path, and registers it in the media storage if it's not a system font.
 * @param nameOrPath name, path or URL of the font family to resolve
 * @param context the context to access the media storage from
 */
internal fun loadFontFamily(
    nameOrPath: String,
    context: MutableContext,
): FontFamily? {
    val fontFamily = FontFamilyResolver.SYSTEM.resolve(nameOrPath, context.fileSystem.workingDirectory)
    if (fontFamily is FontFamily.Media) {
        context.mediaStorage.register(nameOrPath, fontFamily.media)
    }
    return fontFamily
}
