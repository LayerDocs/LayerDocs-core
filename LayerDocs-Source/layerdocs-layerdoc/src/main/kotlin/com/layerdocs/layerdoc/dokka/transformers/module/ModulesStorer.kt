package com.layerdocs.layerdoc.dokka.transformers.module

import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.layerdoc.dokka.transformers.LayerDocDocumentableReplacerTransformer
import com.layerdocs.layerdoc.dokka.util.isOfType
import com.layerdocs.layerdoc.dokka.util.sourcePaths
import org.jetbrains.dokka.model.DProperty
import org.jetbrains.dokka.model.GenericTypeConstructor
import org.jetbrains.dokka.plugability.DokkaContext

/**
 * Transformer that, instead of performing transformations,
 * stores the [com.layerdocs.core.function.library.loader.Module] declarations associated with their source files.
 * @see LayerDocsModulesStorage
 */
class ModulesStorer(
    context: DokkaContext,
) : LayerDocDocumentableReplacerTransformer(context) {
    private fun isModuleDefinition(property: DProperty): Boolean {
        val type = property.type as? GenericTypeConstructor ?: return false
        return type.dri.isOfType<LayerDocsModule>()
    }

    override fun transformProperty(property: DProperty): AnyWithChanges<DProperty> {
        if (!isModuleDefinition(property)) return property.unchanged()

        val dri = property.dri

        property.sourcePaths.singleOrNull()?.let { sourceFile ->
            LayerDocsModulesStorage[sourceFile] = StoredModule(name = property.name, dri = dri)
        }
        return property.unchanged()
    }
}
