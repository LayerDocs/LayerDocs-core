package com.layerdocs.core.pipeline.stages

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.pipeline.PipelineHooks
import com.layerdocs.core.pipeline.stage.PeekPipelineStage
import com.layerdocs.core.pipeline.stage.SharedPipelineData

/**
 * Pipeline stage responsible for traversing the abstract syntax tree (AST).
 *
 * This stage uses a tree iterator to traverse the AST and perform operations on it.
 *
 * @see com.layerdocs.core.context.hooks for tree traversal hooks.
 */
object TreeTraversalStage : PeekPipelineStage<AstRoot> {
    override val hook = PipelineHooks::afterTreeTraversal

    override fun peek(
        input: AstRoot,
        data: SharedPipelineData,
    ) {
        data.context.flavor.treeIteratorFactory
            .default(data.context)
            .traverse(input)
    }
}
