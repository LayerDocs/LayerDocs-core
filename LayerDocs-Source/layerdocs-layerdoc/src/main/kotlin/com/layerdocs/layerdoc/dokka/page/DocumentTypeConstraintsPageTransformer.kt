package com.layerdocs.layerdoc.dokka.page

import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.function.layerdocsName
import com.layerdocs.layerdoc.dokka.transformers.misc.DocumentTargetProperty
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.pages.TextStyle
import org.jetbrains.dokka.plugability.DokkaContext

/**
 * Given a function that is constrained to specific document types,
 * this page transformer explains these constraints in a new section of the documentation page.
 * @see com.layerdocs.layerdoc.dokka.transformers.misc.DocumentTypeConstraintsTransformer
 */
class DocumentTypeConstraintsPageTransformer(
    context: DokkaContext,
) : NewSectionDocumentablePageTransformer<DFunction, List<DocumentType>>("Target", context) {
    override fun extractDocumentable(documentables: List<Documentable>) = documentables.firstOrNull() as? DFunction

    override fun extractData(documentable: DFunction): List<DocumentType>? = documentable.extra[DocumentTargetProperty]?.targets

    override fun createSection(
        data: List<DocumentType>,
        documentable: DFunction,
        builder: PageContentBuilder.DocumentableContentBuilder,
    ) = builder.buildGroup {
        text("This function is ")
        text("only available", styles = setOf(TextStyle.Bold))
        text(" for the following document types: ")
        data.forEachIndexed { index, target ->
            codeInline { text(target.layerdocsName) }
            text(if (index < data.size - 1) ", " else ".")
        }
    }
}
