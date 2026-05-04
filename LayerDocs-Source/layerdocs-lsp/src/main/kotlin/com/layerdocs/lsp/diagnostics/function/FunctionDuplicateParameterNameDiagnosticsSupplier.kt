package com.layerdocs.lsp.diagnostics.function

import com.layerdocs.lsp.diagnostics.AbstractFunctionCallDiagnosticsSupplier
import com.layerdocs.lsp.diagnostics.SimpleDiagnostic
import com.layerdocs.lsp.diagnostics.cause.DuplicateParameterNameDiagnosticCause
import com.layerdocs.lsp.tokenizer.FunctionCall
import com.layerdocs.lsp.tokenizer.FunctionCallToken

/**
 * A diagnostics supplier that checks for duplicate function parameter names in function calls.
 */
class FunctionDuplicateParameterNameDiagnosticsSupplier : AbstractFunctionCallDiagnosticsSupplier() {
    override fun getDiagnostics(
        functionName: String,
        tokens: List<FunctionCallToken>,
        call: FunctionCall,
    ): List<SimpleDiagnostic> =
        tokens
            .asSequence()
            .filter { it.type == FunctionCallToken.Type.PARAMETER_NAME }
            .groupBy { it.lexeme.trim() }
            .filter { (_, tokens) -> tokens.size > 1 }
            .flatMap { (parameterName, tokens) ->
                tokens.map { token ->
                    SimpleDiagnostic(token.range, DuplicateParameterNameDiagnosticCause(parameterName))
                }
            }.toList()
}
