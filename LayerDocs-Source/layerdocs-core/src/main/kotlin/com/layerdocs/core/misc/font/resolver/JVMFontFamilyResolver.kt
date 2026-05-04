package com.layerdocs.core.misc.font.resolver

import com.layerdocs.core.media.ResolvableMedia
import com.layerdocs.core.misc.font.FontFamily
import java.awt.GraphicsEnvironment
import java.io.File

private const val GOOGLE_FONTS_PREFIX = "GoogleFonts:"

/**
 * JVM/AWT implementation of [FontFamilyResolver].
 */
internal object JVMFontFamilyResolver : FontFamilyResolver {
    private fun isSystemFont(name: String) = name in GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames

    override fun resolve(
        nameOrPath: String,
        workingDirectory: File?,
    ): FontFamily? =
        when {
            nameOrPath.startsWith(GOOGLE_FONTS_PREFIX) -> {
                val fontName = nameOrPath.removePrefix(GOOGLE_FONTS_PREFIX)
                FontFamily.GoogleFont(fontName)
            }

            isSystemFont(nameOrPath) -> FontFamily.System(nameOrPath)

            else -> {
                val media = ResolvableMedia(nameOrPath, workingDirectory)
                FontFamily.Media(media, nameOrPath)
            }
        }
}
