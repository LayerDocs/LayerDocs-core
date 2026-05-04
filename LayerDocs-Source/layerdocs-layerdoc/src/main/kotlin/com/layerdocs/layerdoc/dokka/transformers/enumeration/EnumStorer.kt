package com.layerdocs.layerdoc.dokka.transformers.enumeration

import com.layerdocs.layerdoc.dokka.transformers.LayerDocDocumentableReplacerTransformer
import com.layerdocs.layerdoc.dokka.transformers.module.LayerDocsModulesStorage
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.plugability.DokkaContext

/**
 * Transformer that, instead of performing transformations, stores enum declarations within the module this plugin is applied on.
 * @see LayerDocsModulesStorage
 * @see EnumParameterEntryListerTransformer
 */
class EnumStorer(
    context: DokkaContext,
) : LayerDocDocumentableReplacerTransformer(context) {
    override fun transformClassLike(classlike: DClasslike): AnyWithChanges<DClasslike> {
        if (classlike is DEnum) {
            EnumStorage += classlike
        }
        return classlike.unchanged()
    }
}
