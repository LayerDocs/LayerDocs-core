package com.layerdocs.cli.creator.template

import com.layerdocs.core.template.TemplateProcessor

/**
 * Factory that creates one or multiple [TemplateProcessor]s that generate files of a new LayerDocs project.
 * @see TemplateProcessor
 */
interface ProjectCreatorTemplateProcessorFactory {
    /**
     * @return the [TemplateProcessor] that processes a file of a new LayerDocs project
     */
    fun create(): TemplateProcessor

    /**
     * @return a mapping of file names to [TemplateProcessor]s that process files of a new LayerDocs project.
     * The file name `null` is reserved for the main file
     */
    fun createFilenameMappings(): Map<String?, TemplateProcessor> = mapOf(null to create())
}
