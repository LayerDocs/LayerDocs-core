package com.layerdocs.core.misc.color.decoder

import com.layerdocs.core.misc.color.Color
import com.layerdocs.core.misc.color.NamedColor

/**
 * Decodes a [Color] from the name (case-insensitive) of a [NamedColor] (e.g. `red`, `GREEN`, `bLuE`, `aliceblue`).
 */
object NamedColorDecoder : ColorDecoder {
    override fun decode(raw: String): Color? =
        NamedColor.entries
            .find { it.name.equals(raw, ignoreCase = true) }
            ?.color
}
