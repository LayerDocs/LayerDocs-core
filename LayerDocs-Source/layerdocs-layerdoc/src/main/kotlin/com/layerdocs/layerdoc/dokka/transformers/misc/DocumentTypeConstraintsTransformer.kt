package com.layerdocs.layerdoc.dokka.transformers.misc

import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.function.reflect.annotation.NotForDocumentType
import com.layerdocs.core.function.reflect.annotation.OnlyForDocumentType
import com.layerdocs.layerdoc.dokka.transformers.LayerDocDocumentableReplacerTransformer
import com.layerdocs.layerdoc.dokka.util.extractAnnotation
import com.layerdocs.layerdoc.dokka.util.parameterToEnumArray
import com.layerdocs.layerdoc.dokka.util.withAddedExtra
import org.jetbrains.dokka.model.Annotations
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.model.properties.ExtraProperty
import org.jetbrains.dokka.plugability.DokkaContext
import kotlin.reflect.KProperty

/**
 * Extra property that stores the document types a function supports, if specified.
 * @param targets the list of document types the function supports
 */
data class DocumentTargetProperty(
    val targets: List<DocumentType>,
) : ExtraProperty<DFunction> {
    companion object : ExtraProperty.Key<DFunction, DocumentTargetProperty>

    override val key = DocumentTargetProperty
}

/**
 * Given a function annotated with `@OnlyForDocumentType` or `@NotForDocumentType` which defines constraints
 * about the document type the function supports, these transformer add a [DocumentTargetProperty] extra property.
 * @see com.layerdocs.layerdoc.dokka.page.DocumentTypeConstraintsPageTransformer
 * @see OnlyForDocumentType
 * @see NotForDocumentType
 */
object DocumentTypeConstraintsTransformer {
    /**
     * Base class for transformers that specify document type constraints to [DFunction]s,
     * retrieved from annotations in the function declaration.
     * If type constraints are present, the supported document types are stored
     * in the function's extra properties as [DocumentTargetProperty].
     * @param annotationTypesParameterProperty the property of the annotation that contains the document types
     */
    abstract class AbstractTransformer(
        private val annotationTypesParameterProperty: KProperty<Array<out DocumentType>>,
        context: DokkaContext,
    ) : LayerDocDocumentableReplacerTransformer(context) {
        /**
         * @return the annotation extracted from the function declaration
         * which specifies the document type constraints, if any.
         */
        abstract fun extractAnnotation(function: DFunction): Annotations.Annotation?

        /**
         * Given the document types extracted from the annotation, transforms them.
         */
        abstract fun transformTypes(types: List<DocumentType>): List<DocumentType>

        override fun transformFunction(function: DFunction): AnyWithChanges<DFunction> {
            val types: List<DocumentType> =
                extractAnnotation(function)
                    ?.parameterToEnumArray(annotationTypesParameterProperty.name, DocumentType::valueOf)
                    ?.let(::transformTypes)
                    ?: return function.unchanged()

            return function
                .withAddedExtra(DocumentTargetProperty(types))
                .changed()
        }
    }

    /**
     * Given a function annotated with `@OnlyForDocumentType(X)`,
     * this transformer sets the function's constraint to only the document type `X`.
     */
    class Positive(
        context: DokkaContext,
    ) : AbstractTransformer(annotationTypesParameterProperty = OnlyForDocumentType::types, context) {
        override fun extractAnnotation(function: DFunction) = function.extractAnnotation<OnlyForDocumentType>()

        override fun transformTypes(types: List<DocumentType>) = types
    }

    /**
     * Given a function annotated with `@NotForDocumentType(X)`,
     * this transformer sets the function's constraint to all document types except `X`.
     */
    class Negative(
        context: DokkaContext,
    ) : AbstractTransformer(annotationTypesParameterProperty = NotForDocumentType::types, context) {
        override fun extractAnnotation(function: DFunction) = function.extractAnnotation<NotForDocumentType>()

        override fun transformTypes(types: List<DocumentType>) = DocumentType.entries - types
    }
}
