package com.layerdocs.layerdoc.dokka.transformers.enumeration.adapters

import com.layerdocs.layerdoc.dokka.transformers.enumeration.LayerDocEnum
import com.layerdocs.layerdoc.dokka.transformers.enumeration.LayerDocEnumEntry
import org.jetbrains.dokka.links.DRI

/**
 * An adapter for a [LayerDocEnum] that is loaded via reflection.
 * @param cls the enum class
 * @param dri the [DRI] that points to the enum declaration
 */
internal class ReflectionEnumAdapter(
    private val cls: Class<*>,
    private val dri: DRI,
) : LayerDocEnum {
    override val entries: List<LayerDocEnumEntry>
        get() =
            cls.enumConstants
                .filterIsInstance<Enum<*>>()
                .map { ReflectionEnumEntryAdapter(it, dri) }
}

/**
 * An adapter for a [LayerDocEnumEntry] that is loaded via reflection from a [ReflectionEnumAdapter].
 * @param entry the enum entry
 * @param parentDri the [DRI] that points to the enum declaration
 */
internal class ReflectionEnumEntryAdapter(
    private val entry: Enum<*>,
    private val parentDri: DRI,
) : LayerDocEnumEntry {
    override val name: String
        get() = entry.name

    override val dri: DRI
        get() = parentDri.copy(classNames = parentDri.classNames + '.' + entry.name)
}
