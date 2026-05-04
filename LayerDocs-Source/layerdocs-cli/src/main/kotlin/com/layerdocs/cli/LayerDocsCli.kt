package com.layerdocs.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import com.layerdocs.cli.creator.command.CreateProjectCommand
import com.layerdocs.cli.exec.CompileCommand
import com.layerdocs.cli.exec.ReplCommand
import com.layerdocs.cli.lsp.LanguageServerCommand
import com.layerdocs.cli.server.StartWebServerCommand

/**
 * Main command of LayerDocs CLI, which delegates to subcommands.
 */
class LayerDocsCommand : CliktCommand() {
    init {
        val version = this::class.java.getResource("/version.txt")?.readText() ?: "unknown"
        versionOption(version)
    }

    override fun aliases() = mapOf("c" to listOf(CompileCommand().commandName))

    override fun run() {}
}

fun main(args: Array<String>) =
    LayerDocsCommand()
        .subcommands(
            CompileCommand(),
            ReplCommand(),
            StartWebServerCommand(),
            CreateProjectCommand(),
            LanguageServerCommand(),
        ).main(args)
