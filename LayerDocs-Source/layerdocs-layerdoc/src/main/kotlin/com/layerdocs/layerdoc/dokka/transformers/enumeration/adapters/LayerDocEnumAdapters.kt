package com.layerdocs.layerdoc.dokka.transformers.enumeration.adapters

import com.layerdocs.layerdoc.dokka.transformers.enumeration.EnumStorage
import com.layerdocs.layerdoc.dokka.transformers.enumeration.LayerDocEnum
import com.layerdocs.layerdoc.dokka.util.fullyQualifiedReflectionName
import org.jetbrains.dokka.links.DRI

/**
 * Utilities to adapt a [LayerDocEnum] from different sources.
 */
object LayerDocEnumAdapters {
    /**
     * Looks up a [LayerDocEnum] from the given [DRI].
     * - If the enum is declared in the same module, it will be found in the [EnumStorage] as a [DokkaEnumAdapter].
     * - If the enum is declared in a different module that is present in this classpath (e.g. `core`),
     *   it will be loaded via reflection as a [ReflectionEnumAdapter].
     * @param dri the [DRI] that points to the enum declaration.
     * @return a [LayerDocEnum] from the given [DRI], or `null` if it cannot be found.
     */
    fun fromDRI(dri: DRI): LayerDocEnum? =
        EnumStorage.fromDRI(dri)?.let(::DokkaEnumAdapter)
            ?: try {
                Class
                    .forName(dri.fullyQualifiedReflectionName)
                    .takeIf { it.isEnum }
                    ?.let { ReflectionEnumAdapter(it, dri) }
            } catch (_: Exception) {
                null
            }
}
