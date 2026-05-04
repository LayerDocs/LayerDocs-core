package com.layerdocs.lsp.ontype

/**
 * Factory for creating [OnTypeFormattingEditSupplier]s.
 */
object OnTypeFormattingSuppliersFactory {
    fun default(): List<OnTypeFormattingEditSupplier> =
        listOf(
            TrailingSpacesRemoverOnTypeFormattingEditSupplier(),
            LineContinuationAutoIndentOnTypeFormattingEditSupplier(),
        )
}
