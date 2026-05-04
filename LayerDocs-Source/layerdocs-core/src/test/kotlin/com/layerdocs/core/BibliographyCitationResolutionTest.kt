package com.layerdocs.core

import com.layerdocs.core.ast.NestableNode
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.attributes.reference.getDefinition
import com.layerdocs.core.ast.dsl.buildBlock
import com.layerdocs.core.ast.iterator.ObservableAstIterator
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyCitation
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.bibliography.Bibliography
import com.layerdocs.core.bibliography.BibliographyEntry
import com.layerdocs.core.bibliography.style.BibliographyEntryLabelProviderStrategy
import com.layerdocs.core.bibliography.style.BibliographyStyle
import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.context.hooks.reference.BibliographyCitationResolverHook
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import kotlin.test.Test
import kotlin.test.assertEquals

private const val CITATION_KEY = "einstein"

/**
 * Stub [BibliographyStyle] for tests that only need citation resolution, not formatting.
 */
private val stubStyle =
    object : BibliographyStyle {
        override val name = "stub"

        override val labelProvider =
            object : BibliographyEntryLabelProviderStrategy {
                override fun getCitationLabel(entries: List<BibliographyEntry>) = ""

                override fun getListLabel(
                    entry: BibliographyEntry,
                    index: Int,
                ) = "[${index + 1}]"
            }

        override fun contentOf(entry: BibliographyEntry) = emptyList<Node>()
    }

/**
 * Tests for resolving bibliography citations to their bibliography entries.
 */
class BibliographyCitationResolutionTest {
    private val context = MutableContext(LayerDocsFlavor)

    private val bibliographyView =
        BibliographyView(
            bibliography =
                Bibliography(
                    listOf("einstein", "latexcompanion", "knuthwebsite")
                        .associateWith { BibliographyEntry(it) },
                ),
            style = stubStyle,
        )

    private val citation = BibliographyCitation(listOf(CITATION_KEY))

    private fun traverse(root: Node) {
        ObservableAstIterator()
            .attach(BibliographyCitationResolverHook(context))
            .traverse(root as NestableNode)
    }

    @Test
    fun `citation after bibliography`() {
        val root =
            buildBlock {
                root {
                    +bibliographyView
                    +citation
                }
            }

        traverse(root)

        val resolved = citation.getDefinition(context)?.first
        assertEquals(1, resolved?.size)
        assertEquals(bibliographyView.bibliography.entries[CITATION_KEY], resolved?.first())
    }

    @Test
    fun `citation before bibliography`() {
        val root =
            buildBlock {
                root {
                    +citation
                    +bibliographyView
                }
            }

        traverse(root)

        val resolved = citation.getDefinition(context)?.first
        assertEquals(1, resolved?.size)
        assertEquals(bibliographyView.bibliography.entries[CITATION_KEY], resolved?.first())
    }

    @Test
    fun `multi-key citation`() {
        val multiCitation = BibliographyCitation(listOf("einstein", "latexcompanion"))

        val root =
            buildBlock {
                root {
                    +bibliographyView
                    +multiCitation
                }
            }

        traverse(root)

        val resolved = multiCitation.getDefinition(context)?.first
        assertEquals(2, resolved?.size)
        assertEquals(bibliographyView.bibliography.entries["einstein"], resolved?.get(0))
        assertEquals(bibliographyView.bibliography.entries["latexcompanion"], resolved?.get(1))
    }
}
