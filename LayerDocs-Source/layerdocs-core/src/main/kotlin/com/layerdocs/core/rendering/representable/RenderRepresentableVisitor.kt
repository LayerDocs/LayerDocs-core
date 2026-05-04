package com.layerdocs.core.rendering.representable

import com.layerdocs.core.ast.base.block.BlockQuote
import com.layerdocs.core.ast.base.block.Table
import com.layerdocs.core.ast.layerdocs.block.Box
import com.layerdocs.core.ast.layerdocs.block.Clipped
import com.layerdocs.core.ast.layerdocs.block.Container
import com.layerdocs.core.ast.layerdocs.block.NavigationContainer
import com.layerdocs.core.ast.layerdocs.block.SlidesFragment
import com.layerdocs.core.ast.layerdocs.block.Stacked
import com.layerdocs.core.ast.layerdocs.inline.TextTransformData
import com.layerdocs.core.document.layout.caption.CaptionPosition
import com.layerdocs.core.document.layout.page.PageMarginPosition
import com.layerdocs.core.document.layout.page.PageSide
import com.layerdocs.core.document.size.Size
import com.layerdocs.core.document.size.Sizes
import com.layerdocs.core.document.slides.Transition
import com.layerdocs.core.misc.color.Color

/**
 * Visitor that produces representations of each [RenderRepresentable] subtype
 * suitable for the final rendered document.
 */
interface RenderRepresentableVisitor<T> {
    fun visit(color: Color): T

    fun visit(size: Size): T

    fun visit(sizes: Sizes): T

    fun visit(alignment: Table.Alignment): T

    fun visit(position: CaptionPosition): T

    fun visit(borderStyle: Container.BorderStyle): T

    fun visit(alignment: Container.Alignment): T

    fun visit(alignment: Container.TextAlignment): T

    fun visit(alignment: Container.FloatAlignment): T

    fun visit(stackLayout: Stacked.Layout): T

    fun visit(alignment: Stacked.MainAxisAlignment): T

    fun visit(alignment: Stacked.CrossAxisAlignment): T

    fun visit(clip: Clipped.Clip): T

    fun visit(quoteType: BlockQuote.Type): T

    fun visit(boxType: Box.Type): T

    fun visit(navigationRole: NavigationContainer.Role): T

    fun visit(position: PageMarginPosition): T

    fun visit(transition: Transition.Style): T

    fun visit(speed: Transition.Speed): T

    fun visit(behavior: SlidesFragment.Behavior): T

    fun visit(size: TextTransformData.Size): T

    fun visit(weight: TextTransformData.Weight): T

    fun visit(style: TextTransformData.Style): T

    fun visit(decoration: TextTransformData.Decoration): T

    fun visit(case: TextTransformData.Case): T

    fun visit(variant: TextTransformData.Variant): T

    fun visit(script: TextTransformData.Script): T

    fun visit(pageSide: PageSide): T
}
