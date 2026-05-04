package com.layerdocs.core.pipeline.stages

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.Node
import com.layerdocs.core.lexer.Token
import com.layerdocs.core.lexer.acceptAll
import com.layerdocs.core.pipeline.PipelineHooks
import com.layerdocs.core.pipeline.stage.PipelineStage
import com.layerdocs.core.pipeline.stage.SharedPipelineData
import com.layerdocs.core.visitor.token.TokenVisitor

/**
 * Pipeline stage responsible for parsing tokens into an abstract syntax tree (AST).
 *
 * This stage takes a sequence of tokens (produced by the [LexingStage]) as input and
 * produces an [AstRoot] as output.
 *
 * The AST represents the hierarchical structure of the document and is used by
 * subsequent stages for further processing and rendering.
 */
object ParsingStage : PipelineStage<Sequence<Token>, AstRoot> {
    override val hook = PipelineHooks::afterParsing

    override fun process(
        input: Sequence<Token>,
        data: SharedPipelineData,
    ): AstRoot {
        val parser: TokenVisitor<Node> =
            data.context.flavor.parserFactory
                .newParser(data.context)

        return AstRoot(children = input.acceptAll(parser))
    }
}
