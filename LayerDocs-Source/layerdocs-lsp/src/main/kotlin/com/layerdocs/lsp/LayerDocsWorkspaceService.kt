package com.layerdocs.lsp

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.services.WorkspaceService

/**
 *
 */
class LayerDocsWorkspaceService(
    private val server: LayerDocsLanguageServer,
) : WorkspaceService {
    override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {
        server.log("Configuration changed: ${params?.settings}")
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
        server.log("Watched files changed: ${params?.changes?.joinToString { it.uri }}")
    }
}
