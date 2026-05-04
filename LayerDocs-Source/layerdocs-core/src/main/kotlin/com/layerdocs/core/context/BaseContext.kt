package com.layerdocs.core.context

import com.layerdocs.core.ast.attributes.AstAttributes
import com.layerdocs.core.ast.layerdocs.FunctionCallNode
import com.layerdocs.core.context.file.FileSystem
import com.layerdocs.core.context.file.RootGranularity
import com.layerdocs.core.context.file.SimpleFileSystem
import com.layerdocs.core.context.file.getRootFileSystem
import com.layerdocs.core.context.options.ContextOptions
import com.layerdocs.core.context.options.MutableContextOptions
import com.layerdocs.core.context.subdocument.SubdocumentsData
import com.layerdocs.core.document.DocumentInfo
import com.layerdocs.core.document.sub.Subdocument
import com.layerdocs.core.flavor.MarkdownFlavor
import com.layerdocs.core.function.Function
import com.layerdocs.core.function.call.FunctionCall
import com.layerdocs.core.function.call.UncheckedFunctionCall
import com.layerdocs.core.function.library.Library
import com.layerdocs.core.graph.DirectedGraph
import com.layerdocs.core.graph.VisitableOnceGraph
import com.layerdocs.core.graph.visitableOnce
import com.layerdocs.core.localization.Locale
import com.layerdocs.core.localization.LocalizationKeyNotFoundException
import com.layerdocs.core.localization.LocalizationLocaleNotFoundException
import com.layerdocs.core.localization.LocalizationTable
import com.layerdocs.core.localization.LocalizationTableNotFoundException
import com.layerdocs.core.media.storage.MutableMediaStorage
import com.layerdocs.core.media.storage.ReadOnlyMediaStorage
import com.layerdocs.core.permissions.Permission
import com.layerdocs.core.pipeline.Pipeline
import com.layerdocs.core.pipeline.Pipelines

/**
 * An immutable [Context] implementation.
 * This might be used in tests as a toy context, but in a concrete execution, its mutable subclass [MutableContext] is used.
 * @param attributes attributes of the node tree, produced by the parsing stage
 * @param flavor Markdown flavor used for this pipeline. It specifies how to produce the needed components
 * @param libraries loaded libraries to look up functions from
 * @param subdocument the subdocument this context is processing
 */
open class BaseContext(
    override val attributes: AstAttributes,
    override val flavor: MarkdownFlavor,
    override val libraries: Set<Library> = emptySet(),
    override val subdocument: Subdocument = Subdocument.Root,
) : Context {
    override val attachedPipeline: Pipeline?
        get() = Pipelines.getAttachedPipeline(this)

    override val documentInfo = DocumentInfo()

    override val options: ContextOptions = MutableContextOptions()

    override val loadableLibraries = emptySet<Library>()

    override val localizationTables = emptyMap<String, LocalizationTable>()

    override val mediaStorage: ReadOnlyMediaStorage by lazy {
        MutableMediaStorage(options, permissionHolder = this)
    }

    override val sharedSubdocumentsData: SubdocumentsData<VisitableOnceGraph<Subdocument>> =
        SubdocumentsData(
            graph = DirectedGraph<Subdocument>().visitableOnce,
            withContexts = mapOf(subdocument to this),
        )

    override val fileSystem: FileSystem by lazy {
        val workingDirectory =
            (subdocument as? Subdocument.Resource)?.workingDirectory
                ?: attachedPipeline?.options?.workingDirectory
        SimpleFileSystem(workingDirectory)
    }

    override val permissions: Set<Permission> by lazy {
        attachedPipeline?.options?.permissions.orEmpty()
    }

    override val rootFileSystem: FileSystem? by lazy {
        this.getRootFileSystem(granularity = RootGranularity.PROJECT)
    }

    override fun getFunctionByName(name: String): Function<*>? =
        libraries
            .asSequence()
            .flatMap { it.functions }
            .find { it.name == name }

    override fun resolve(call: FunctionCallNode): FunctionCall<*>? {
        val function = getFunctionByName(call.name)

        return function?.let {
            FunctionCall(
                it,
                call.arguments,
                context = this,
                sourceNode = call,
            )
        }
    }

    override fun resolveUnchecked(call: FunctionCallNode): UncheckedFunctionCall<*> = UncheckedFunctionCall(call.name) { resolve(call) }

    override fun localize(
        tableName: String,
        key: String,
        locale: Locale,
    ): String {
        val table = localizationTables[tableName] ?: throw LocalizationTableNotFoundException(tableName)
        val entries = table[locale] ?: throw LocalizationLocaleNotFoundException(tableName, locale)
        return entries[key.lowercase()] ?: entries[key]
            ?: throw LocalizationKeyNotFoundException(tableName, locale, key)
    }

    override fun fork(): ScopeContext = throw UnsupportedOperationException("Forking is not supported in BaseContext")
}
