package com.layerdocs.layerdoc.dokka.transformers.suppress

import com.layerdocs.core.function.reflect.annotation.Injected
import com.layerdocs.layerdoc.dokka.transformers.LayerDocDocumentableReplacerTransformer
import com.layerdocs.layerdoc.dokka.util.hasAnnotation
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.plugability.DokkaContext

/**
 * Transformer that suppresses parameters annotated with `@Injected` in the generated documentation.
 */
class SuppressInjectedTransformer(
    context: DokkaContext,
) : LayerDocDocumentableReplacerTransformer(context) {
    override fun transformFunction(function: DFunction) =
        function
            .copy(parameters = function.parameters.filterNot { it.hasAnnotation<Injected>() })
            .let {
                it.changed(changed = it.parameters.size != function.parameters.size)
            }
}
