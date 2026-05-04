package com.layerdocs.core.bibliography.style.csl

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.bibliography.Bibliography
import com.layerdocs.core.bibliography.BibliographyEntry
import com.layerdocs.core.bibliography.style.BibliographyEntryLabelProviderStrategy
import com.layerdocs.core.bibliography.style.BibliographyStyle
import com.layerdocs.core.localization.Locale
import com.layerdocs.core.util.node.toPlainText
import de.undercouch.citeproc.BibliographyFileReader
import de.undercouch.citeproc.CSL
import de.undercouch.citeproc.ItemDataProvider
import java.io.IOException
import java.io.InputStream

/**
 * A [BibliographyStyle] backed by a [CSL](https://citationstyles.org) style definition,
 * powered by [citeproc-java](https://github.com/michel-kraemer/citeproc-java).
 *
 * This enables support for a curated selection of citation styles from the
 * [CSL Style Repository](https://github.com/citation-style-language/styles),
 * including BibTeX, CSL JSON, YAML, EndNote, and RIS bibliography sources.
 *
 * Citation label and entry content formatting are delegated to citeproc-java,
 * which processes the CSL XML style definition and produces structured output
 * converted to LayerDocs AST nodes via [LayerDocsCslFormat] and [CslTokenConverter].
 *
 * @param cslStyleName the CSL style identifier (e.g. `"apa"`, `"ieee"`, `"chicago-author-date"`)
 * @param provider the item data provider supplying bibliography data to citeproc-java
 * @param locale optional [RFC 4646](https://www.rfc-editor.org/rfc/rfc4646) locale tag
 *               (e.g. `"en-US"`, `"de-DE"`). Controls localized terms such as "and"/"und",
 *               month names, and ordinal suffixes. When `null`, the style's default locale is used,
 *               falling back to `"en-US"`.
 * @see LayerDocsCslFormat
 * @see CslTokenConverter
 */
class CslBibliographyStyle(
    private val cslStyleName: String,
    private val provider: ItemDataProvider,
    locale: String? = null,
) : BibliographyStyle {
    private val format = LayerDocsCslFormat()

    private val csl =
        CSL(provider, cslStyleName, locale).apply {
            setOutputFormat(format)
            registerCitationItems(provider.ids)
        }

    /**
     * The [Bibliography] derived from the provider's entry IDs.
     */
    val bibliography: Bibliography by lazy {
        Bibliography(
            provider.ids.associateWith(::BibliographyEntry),
        )
    }

    /**
     * Lazily formatted bibliography entries, mapping each citation key
     * to its [FormattedBibliographyEntry] (label + content).
     *
     * Triggering this lazy value calls [CSL.makeBibliography], which invokes
     * [LayerDocsCslFormat.doFormatBibliographyEntry] for each entry sequentially.
     * The accumulated results are then matched to provider IDs by position.
     */
    private val formattedEntries: Map<String, FormattedBibliographyEntry> by lazy {
        format.bibliographyEntries.clear()
        csl.makeBibliography()
        provider.ids.zip(format.bibliographyEntries).toMap()
    }

    override val name: String
        get() = cslStyleName

    override val labelProvider =
        object : BibliographyEntryLabelProviderStrategy {
            override fun getCitationLabel(entries: List<BibliographyEntry>): String {
                csl.makeCitation(*entries.map { it.citationKey }.toTypedArray())
                return format.lastCitationResult.toPlainText().ifBlank { "[?]" }
            }

            override fun getListLabel(
                entry: BibliographyEntry,
                index: Int,
            ): String = formattedEntries[entry.citationKey]?.label ?: ""
        }

    override fun contentOf(entry: BibliographyEntry): InlineContent = formattedEntries[entry.citationKey]?.content ?: emptyList()

    companion object {
        /**
         * Reads a bibliography file and creates a [CslBibliographyStyle].
         * Supports BibTeX (`.bib`), CSL JSON, YAML, EndNote, and RIS formats.
         * @param cslStyleName the CSL style identifier
         * @param input the input stream for the bibliography source
         * @param filename the filename hint for format detection
         * @param locale optional [Locale] for localized terms (e.g. "and"/"und", month names).
         *               When `null`, the style's default locale is used
         * @return a new [CslBibliographyStyle]
         */
        fun from(
            cslStyleName: String,
            input: InputStream,
            filename: String,
            locale: Locale? = null,
        ): CslBibliographyStyle {
            val provider = BibliographyFileReader().readBibliographyFile(input, filename)
            return try {
                CslBibliographyStyle(cslStyleName, provider, locale?.tag)
            } catch (e: IOException) {
                throw IllegalArgumentException(
                    "Bibliography style '$cslStyleName' does not exist or failed to load. " +
                        "See https://layerdocs.com/wiki/bibliography for a list of available styles.",
                    e,
                )
            }
        }
    }
}
