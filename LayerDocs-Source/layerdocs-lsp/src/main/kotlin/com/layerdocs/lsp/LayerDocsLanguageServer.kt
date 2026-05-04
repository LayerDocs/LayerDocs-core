package com.layerdocs.lsp

import com.layerdocs.lsp.cache.CacheableFunctionCatalogue
import com.layerdocs.lsp.completion.CompletionSuppliersFactory
import com.layerdocs.lsp.diagnostics.DiagnosticsSuppliersFactory
import com.layerdocs.lsp.highlight.SemanticTokensSuppliersFactory
import com.layerdocs.lsp.highlight.TokenType
import com.layerdocs.lsp.hover.HoverSuppliersFactory
import com.layerdocs.lsp.ontype.OnTypeFormattingSuppliersFactory
import com.layerdocs.lsp.pattern.LayerDocsPatterns
import org.eclipse.lsp4j.CompletionOptions
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DocumentOnTypeFormattingOptions
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.SemanticTokensLegend
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * LayerDocs Language Server implementation.
 * @param layerdocsDirectory the directory containing the LayerDocs distribution, if available
 */
class LayerDocsLanguageServer(
    private val layerdocsDirectory: File?,
) : LanguageServer,
    LanguageClientAware {
    private val textDocumentService: TextDocumentService =
        LayerDocsTextDocumentService(
            this,
            CompletionSuppliersFactory.default(this),
            SemanticTokensSuppliersFactory.default(),
            HoverSuppliersFactory.default(this),
            DiagnosticsSuppliersFactory.default(this),
            OnTypeFormattingSuppliersFactory.default(),
        )

    private val completionTriggers =
        with(LayerDocsPatterns.FunctionCall) {
            listOf(
                BEGIN,
                CHAIN_SEPARATOR.last().toString(),
                ARGUMENT_BEGIN,
            )
        }

    private val onTypeFormattingOptions = DocumentOnTypeFormattingOptions("\n")

    private val workspaceService: WorkspaceService = LayerDocsWorkspaceService(this)

    private lateinit var client: LanguageClient

    /**
     * The directory containing the documentation files, if available.
     * This is located in the LayerDocs distribution.
     */
    val docsDirectory: File?
        get() = layerdocsDirectory?.resolve("docs")?.takeIf { it.isDirectory }

    /**
     * @return the documentation directory, or throws an exception if it's not available
     * @throws IllegalStateException if the documentation directory does not exist
     */
    fun docsDirectoryOrThrow(): File = requireNotNull(docsDirectory) { "Documentation directory is not available" }

    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult?>? {
        val legend =
            SemanticTokensLegend(
                TokenType.legend,
                emptyList(),
            )

        val serverCaps =
            ServerCapabilities().apply {
                textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
                completionProvider = CompletionOptions(true, completionTriggers)
                hoverProvider = Either.forLeft(true)
                semanticTokensProvider = SemanticTokensWithRegistrationOptions(legend, true, null)
                documentOnTypeFormattingProvider = onTypeFormattingOptions
            }
        val response = InitializeResult(serverCaps)

        // Caching the available function catalogue for improved performance.
        thread {
            docsDirectory?.let(CacheableFunctionCatalogue::storeCatalogue)
        }

        return CompletableFuture.completedFuture(response)
    }

    override fun shutdown(): CompletableFuture<in Any>? = CompletableFuture.completedFuture(null)

    override fun exit() = exitProcess(0)

    override fun getTextDocumentService() = textDocumentService

    override fun getWorkspaceService() = workspaceService

    override fun connect(client: LanguageClient?) {
        this.client = client ?: throw IllegalStateException("Language client cannot be null")
    }

    /**
     * Publishes diagnostics to the client.
     * @param uri the document URI
     * @param diagnostics the list of diagnostics
     */
    fun publishDiagnostics(
        uri: String,
        diagnostics: List<Diagnostic>,
    ) {
        client.publishDiagnostics(PublishDiagnosticsParams(uri, diagnostics))
    }

    /**
     * Logs a message to the client.
     * @param message the message to log
     */
    fun log(message: String) {
        client.logMessage(MessageParams(MessageType.Log, message))
    }
}
