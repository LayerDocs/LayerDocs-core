package com.layerdocs.layerdoc.reader.dokka

import com.layerdocs.layerdoc.reader.DocsWalker
import java.io.File

/**
 * A directory with this name is a LayerDocs module.
 */
private const val MODULE_DIR_NAME = "module"

/**
 * Recursive walker of Dokka HTML files.
 */
class DokkaHtmlWalker(
    private val root: File,
) : DocsWalker<DokkaHtmlContentExtractor> {
    // e.g. com.layerdocs.stdlib.module.String/lowercase.html => String
    private val File.layerdocsModuleName: String?
        get() =
            parentFile.name
                .split('.')
                .takeIf { it.getOrNull(it.size - 2) == MODULE_DIR_NAME }
                ?.lastOrNull()

    /**
     * Recursively scans Dokka HTML files in the given root directory.
     */
    override fun walk(): Sequence<DocsWalker.Result<DokkaHtmlContentExtractor>> =
        root
            .walkTopDown()
            .asSequence()
            .filter { it.isFile }
            .filter { it.extension == "html" }
            .filterNot { it.name == "index.html" }
            .map { file ->
                DocsWalker.Result(
                    name = file.nameWithoutExtension,
                    moduleName = file.layerdocsModuleName,
                    extractor = { DokkaHtmlContentExtractor(file.readText()) },
                )
            }
}
