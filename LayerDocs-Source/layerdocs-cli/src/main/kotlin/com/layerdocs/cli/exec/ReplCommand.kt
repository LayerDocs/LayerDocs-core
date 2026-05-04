package com.layerdocs.cli.exec

import com.layerdocs.cli.CliOptions
import com.layerdocs.cli.exec.strategy.ReplExecutionStrategy

/**
 * Command to start LayerDocs in interactive REPL mode.
 * @see ReplExecutionStrategy
 */
class ReplCommand : ExecuteCommand("repl") {
    override fun createExecutionStrategy(cliOptions: CliOptions) = ReplExecutionStrategy()
}
