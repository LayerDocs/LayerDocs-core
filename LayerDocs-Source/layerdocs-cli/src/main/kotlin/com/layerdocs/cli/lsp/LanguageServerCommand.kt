package com.layerdocs.cli.lsp

import com.github.ajalt.clikt.core.CliktCommand
import com.layerdocs.installlayout.InstallLayout
import com.layerdocs.lsp.LayerDocsLanguageServerLauncher

/**
 * Command to start the LayerDocs Language Server.
 */
class LanguageServerCommand : CliktCommand("language-server") {
    override fun run() {
        // The distribution directory which contains lib/, docs/, etc.
        val layerdocsDirectory = InstallLayout.getOrNull?.file?.parentFile
        LayerDocsLanguageServerLauncher(layerdocsDirectory).startListening()
    }
}
