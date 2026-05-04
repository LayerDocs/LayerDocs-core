package com.layerdocs.core.ast.layerdocs.block

import com.layerdocs.core.ast.InlineContent
import com.layerdocs.core.ast.Node
import com.layerdocs.core.ast.SingleChildNestableNode
import com.layerdocs.core.ast.attributes.localization.LocalizedKind
import com.layerdocs.core.ast.attributes.localization.LocalizedKindKeys
import com.layerdocs.core.ast.attributes.location.LocationTrackableNode
import com.layerdocs.core.ast.base.inline.Image
import com.layerdocs.core.ast.layerdocs.CaptionableNode
import com.layerdocs.core.ast.layerdocs.reference.CrossReferenceableNode
import com.layerdocs.core.util.node.group
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * A block which displays a single child, with an optional caption.
 * If a [caption] is provided or [referenceId] is set, the block is numbered.
 * @param child wrapped child
 * @param caption optional inline caption of the figure block
 * @param referenceId optional ID that can be cross-referenced via a [com.layerdocs.core.ast.layerdocs.reference.CrossReference]
 * @param T type of the wrapped child node
 */
open class Figure<T : Node>(
    override val child: T,
    override val caption: InlineContent? = null,
    override val referenceId: String? = null,
) : SingleChildNestableNode<T>,
    LocationTrackableNode,
    CrossReferenceableNode,
    CaptionableNode,
    LocalizedKind {
    override val children: List<Node>
        get() = listOf(child, caption.group())

    override val kindLocalizationKey: String
        get() = LocalizedKindKeys.FIGURE

    /**
     * A figure is numbered if it has either a [caption] or a [referenceId].
     */
    override val canTrackLocation: Boolean
        get() = caption != null || referenceId != null

    override fun <T> accept(visitor: NodeVisitor<T>): T = visitor.visit(this)
}

/**
 * An optionally-numbered block which displays a single image, with an optional caption.
 * The caption of the image matches the image title, if any.
 * @param child wrapped image
 * @see Image
 */
class ImageFigure(
    child: Image,
) : Figure<Image>(
        child,
        caption = child.link.title,
        referenceId = child.referenceId,
    )
