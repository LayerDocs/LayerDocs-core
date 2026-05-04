package com.layerdocs.core.document.layout.caption

import com.layerdocs.core.rendering.representable.RenderRepresentable
import com.layerdocs.core.rendering.representable.RenderRepresentableVisitor

/**
 * Possible positions of captions, relative to the element they describe.
 * @see CaptionPositionInfo
 */
enum class CaptionPosition : RenderRepresentable {
    TOP,
    BOTTOM,
    ;

    override fun <T> accept(visitor: RenderRepresentableVisitor<T>): T = visitor.visit(this)
}
