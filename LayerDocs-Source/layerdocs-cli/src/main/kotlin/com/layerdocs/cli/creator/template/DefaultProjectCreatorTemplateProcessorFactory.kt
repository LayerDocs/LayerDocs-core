package com.layerdocs.cli.creator.template

import com.layerdocs.core.document.DocumentInfo
import com.layerdocs.core.document.DocumentType
import com.layerdocs.core.function.layerdocsName
import com.layerdocs.core.template.TemplateProcessor

private const val TEMPLATE = "/creator/main.qd.jte"

/**
 * Implementation of [ProjectCreatorTemplateProcessorFactory]
 * based on the default template, which relies on document information
 * to fill placeholders.
 * @param info document information to inject into the template
 * @param template name of the template resource to use
 * @see ProjectCreatorTemplatePlaceholders
 */
class DefaultProjectCreatorTemplateProcessorFactory(
    private val info: DocumentInfo,
    private val template: String = TEMPLATE,
) : ProjectCreatorTemplateProcessorFactory {
    override fun create(): TemplateProcessor =
        with(ProjectCreatorTemplatePlaceholders) {
            TemplateProcessor.fromResourceName(template).apply {
                optionalValue(NAME, info.name)
                optionalValue(DESCRIPTION, info.description)
                conditional(KEYWORDS, info.keywords.isNotEmpty())
                iterable(KEYWORDS, info.keywords)
                conditional(AUTHORS, info.authors.isNotEmpty())
                iterable(AUTHORS, info.authors.map { it.name })
                optionalValue(TYPE, info.type.layerdocsName)
                conditional(IS_DOCS, info.type == DocumentType.DOCS)
                optionalValue(LANGUAGE, info.locale?.displayName)
                conditional(HAS_THEME, info.theme?.hasComponent == true)
                optionalValue(COLOR_THEME, info.theme?.color)
                optionalValue(LAYOUT_THEME, info.theme?.layout)
                conditional(USE_PAGE_COUNTER, info.type == DocumentType.PAGED)
            }
        }
}
