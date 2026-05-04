package com.layerdocs.layerdoc.dokka.transformers.enumeration.adapters

import com.layerdocs.layerdoc.dokka.transformers.enumeration.LayerDocEnum
import com.layerdocs.layerdoc.dokka.transformers.enumeration.LayerDocEnumEntry
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DEnumEntry

/**
 * An adapter for a [LayerDocEnum] from a Dokka-loaded enum.
 * @param enum the enum model
 */
internal class DokkaEnumAdapter(
    private val enum: DEnum,
) : LayerDocEnum {
    override val entries: List<LayerDocEnumEntry>
        get() = enum.entries.map(::DokkaEnumEntryAdapter)
}

/**
 * An adapter for a [LayerDocEnumEntry] from a Dokka-loaded enum entry.
 * @param entry the enum entry model
 */
internal class DokkaEnumEntryAdapter(
    private val entry: DEnumEntry,
) : LayerDocEnumEntry {
    override val name: String
        get() = entry.name

    override val dri: DRI
        get() = entry.dri
}
