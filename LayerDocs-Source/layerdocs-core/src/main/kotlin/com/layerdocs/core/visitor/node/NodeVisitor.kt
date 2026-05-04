package com.layerdocs.core.visitor.node

import com.layerdocs.core.ast.AstRoot
import com.layerdocs.core.ast.base.block.BlankNode
import com.layerdocs.core.ast.base.block.BlockQuote
import com.layerdocs.core.ast.base.block.Code
import com.layerdocs.core.ast.base.block.FootnoteDefinition
import com.layerdocs.core.ast.base.block.Heading
import com.layerdocs.core.ast.base.block.HorizontalRule
import com.layerdocs.core.ast.base.block.Html
import com.layerdocs.core.ast.base.block.LinkDefinition
import com.layerdocs.core.ast.base.block.Newline
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.OrderedList
import com.layerdocs.core.ast.base.block.list.UnorderedList
import com.layerdocs.core.ast.base.inline.CheckBox
import com.layerdocs.core.ast.base.inline.CodeSpan
import com.layerdocs.core.ast.base.inline.Comment
import com.layerdocs.core.ast.base.inline.CriticalContent
import com.layerdocs.core.ast.base.inline.Emphasis
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.base.inline.LineBreak
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.base.inline.ReferenceFootnote
import com.layerdocs.core.ast.base.inline.ReferenceImage
import com.layerdocs.core.ast.base.inline.ReferenceLink
import com.layerdocs.core.ast.base.inline.Strikethrough
import com.layerdocs.core.ast.base.inline.Strong
import com.layerdocs.core.ast.base.inline.StrongEmphasis
import com.layerdocs.core.ast.base.inline.SubdocumentLink
import com.layerdocs.core.ast.base.inline.Text
import com.layerdocs.core.ast.layerdocs.FunctionCallNode
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyCitation
import com.layerdocs.core.ast.layerdocs.bibliography.BibliographyView
import com.layerdocs.core.ast.layerdocs.block.Box
import com.layerdocs.core.ast.layerdocs.block.Clipped
import com.layerdocs.core.ast.layerdocs.block.Collapse
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.ast.layerdocs.block.Figure
import com.layerdocs.core.ast.layerdocs.block.FileTree
import com.layerdocs.core.ast.layerdocs.block.Landscape
import com.layerdocs.core.ast.layerdocs.block.Math
import com.layerdocs.core.ast.layerdocs.block.MermaidDiagram
import com.layerdocs.core.ast.layerdocs.block.NavigationContainer
import com.layerdocs.core.ast.layerdocs.block.Numbered
import com.layerdocs.core.ast.layerdocs.block.PageBreak
import com.layerdocs.core.ast.layerdocs.block.SlidesFragment
import com.layerdocs.core.ast.layerdocs.block.SlidesSpeakerNote
import com.layerdocs.core.ast.layerdocs.block.Stacked
import com.layerdocs.core.ast.layerdocs.block.SubdocumentGraph
import com.layerdocs.core.ast.layerdocs.block.toc.TableOfContentsView
import com.layerdocs.core.ast.layerdocs.inline.IconImage
import com.layerdocs.core.ast.layerdocs.inline.InlineCollapse
import com.layerdocs.core.ast.layerdocs.inline.Keybinding
import com.layerdocs.core.ast.layerdocs.inline.LastHeading
import com.layerdocs.core.ast.layerdocs.inline.MathSpan
import com.layerdocs.core.ast.layerdocs.inline.PageCounter
import com.layerdocs.core.ast.layerdocs.inline.TextSymbol
import com.layerdocs.core.ast.layerdocs.inline.TextTransform
import com.layerdocs.core.ast.layerdocs.inline.Whitespace
import com.layerdocs.core.ast.layerdocs.invisible.PageMarginContentInitializer
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberFormatter
import com.layerdocs.core.ast.layerdocs.invisible.PageNumberReset
import com.layerdocs.core.ast.layerdocs.invisible.SlidesConfigurationInitializer
import com.layerdocs.core.ast.layerdocs.reference.CrossReference

/**
 * A visitor for [com.layerdocs.core.ast.Node]s.
 * @param T output type of the `visit` methods
 */
interface NodeVisitor<T> {
    fun visit(node: AstRoot): T

    // Base block

    fun visit(node: Newline): T

    fun visit(node: Code): T

    fun visit(node: HorizontalRule): T

    fun visit(node: Heading): T

    fun visit(node: LinkDefinition): T

    fun visit(node: FootnoteDefinition): T

    fun visit(node: OrderedList): T

    fun visit(node: UnorderedList): T

    fun visit(node: ListItem): T

    fun visit(node: Html): T

    fun visit(node: Table): T

    fun visit(node: Paragraph): T

    fun visit(node: BlockQuote): T

    fun visit(node: BlankNode): T

    // Base inline

    fun visit(node: Comment): T

    fun visit(node: LineBreak): T

    fun visit(node: CriticalContent): T

    fun visit(node: Link): T

    fun visit(node: ReferenceLink): T

    fun visit(node: SubdocumentLink): T

    fun visit(node: ReferenceFootnote): T

    fun visit(node: Image): T

    fun visit(node: ReferenceImage): T

    fun visit(node: CheckBox): T

    fun visit(node: Text): T

    fun visit(node: TextSymbol): T

    fun visit(node: CodeSpan): T

    fun visit(node: Emphasis): T

    fun visit(node: Strong): T

    fun visit(node: StrongEmphasis): T

    fun visit(node: Strikethrough): T

    // LayerDocs extensions

    fun visit(node: FunctionCallNode): T

    // LayerDocs block

    fun visit(node: Figure<*>): T

    fun visit(node: PageBreak): T

    fun visit(node: Math): T

    fun visit(node: Container): T

    fun visit(node: Stacked): T

    fun visit(node: Numbered): T

    fun visit(node: Landscape): T

    fun visit(node: Clipped): T

    fun visit(node: Box): T

    fun visit(node: Collapse): T

    fun visit(node: Whitespace): T

    fun visit(node: NavigationContainer): T

    fun visit(node: TableOfContentsView): T

    fun visit(node: BibliographyView): T

    fun visit(node: MermaidDiagram): T

    fun visit(node: FileTree): T

    fun visit(node: SubdocumentGraph): T

    // LayerDocs inline

    fun visit(node: MathSpan): T

    fun visit(node: TextTransform): T

    fun visit(node: IconImage): T

    fun visit(node: InlineCollapse): T

    fun visit(node: Keybinding): T

    fun visit(node: PageCounter): T

    fun visit(node: LastHeading): T

    fun visit(node: CrossReference): T

    fun visit(node: BibliographyCitation): T

    fun visit(node: SlidesFragment): T

    fun visit(node: SlidesSpeakerNote): T

    // LayerDocs invisible nodes

    fun visit(node: PageMarginContentInitializer): T

    fun visit(node: PageNumberFormatter): T

    fun visit(node: PageNumberReset): T

    fun visit(node: SlidesConfigurationInitializer): T
}
