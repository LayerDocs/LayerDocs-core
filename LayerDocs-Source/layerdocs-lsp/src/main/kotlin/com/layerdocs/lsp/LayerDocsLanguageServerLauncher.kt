package com.layerdocs.lsp

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import java.io.File

/**
 * Launcher for the LayerDocs Language Server.
 * @param layerdocsDirectory the directory containing the LayerDocs distribution, if available
 */
class LayerDocsLanguageServerLauncher(
    layerdocsDirectory: File?,
) {
    private val languageServer = LayerDocsLanguageServer(layerdocsDirectory)

    private val launcher by lazy {
        Launcher
            .Builder<LanguageClient>()
            .setLocalService(languageServer)
            .setRemoteInterface(LanguageClient::class.java)
            .setInput(System.`in`)
            .setOutput(System.out)
            .create()
            .let(::requireNotNull)
    }

    fun startListening() {
        val client: LanguageClient = requireNotNull(launcher.remoteProxy)
        languageServer.connect(client)

        launcher.startListening()
    }
}
