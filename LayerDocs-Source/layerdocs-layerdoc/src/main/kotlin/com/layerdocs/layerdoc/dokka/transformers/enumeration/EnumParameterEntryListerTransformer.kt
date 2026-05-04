package com.layerdocs.layerdoc.dokka.transformers.enumeration

import com.layerdocs.core.function.toLayerDocsNamingFormat
import com.layerdocs.layerdoc.dokka.kdoc.buildDocTags
import com.layerdocs.layerdoc.dokka.transformers.LayerDocParameterDocumentationTransformer
import com.layerdocs.layerdoc.dokka.transformers.enumeration.adapters.LayerDocEnumAdapters
import com.layerdocs.layerdoc.dokka.util.scrapingAnchor
import com.layerdocs.layerdoc.reader.anchors.Anchors
import org.jetbrains.dokka.base.signatures.KotlinSignatureUtils.driOrNull
import org.jetbrains.dokka.model.DParameter
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.plugability.DokkaContext

/**
 * A transformer that, given a parameter that expects an enum value,
 * lists the enum entries in its documentation.
 */
class EnumParameterEntryListerTransformer(
    context: DokkaContext,
) : LayerDocParameterDocumentationTransformer<LayerDocEnum>(context) {
    /**
     * @return the enum type of the parameter, if it is an enum
     */
    override fun extractValue(parameter: DParameter): LayerDocEnum? = parameter.type.driOrNull?.let(LayerDocEnumAdapters::fromDRI)

    override fun createNewDocumentation(value: LayerDocEnum): List<DocTag> =
        buildDocTags {
            h4 { text("Values") }
            scrapingAnchor(Anchors.VALUES)
            unorderedList {
                value.entries.forEach { entry ->
                    listItem {
                        link(dri = entry.dri) {
                            codeInline(entry.name.toLayerDocsNamingFormat())
                        }
                    }
                }
            }
        }

    override fun mergeDocumentationContent(
        old: List<DocTag>,
        new: List<DocTag>,
    ) = old + new
}
