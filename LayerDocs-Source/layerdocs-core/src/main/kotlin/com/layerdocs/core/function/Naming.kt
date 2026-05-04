package com.layerdocs.core.function

/**
 * @return [this] string reformatted in LayerDocs format (lowercase, no underscores). i.e. `SPACE_AROUND` -> `spacearound`
 */
fun String.toLayerDocsNamingFormat(): String = lowercase().replace("_", "")

/**
 * @return [this] enum's name in LayerDocs format (lowercase, no underscores). i.e. `SPACE_AROUND` -> `spacearound`
 */
val Enum<*>.layerdocsName: String
    get() = name.toLayerDocsNamingFormat()
