package com.layerdocs.rendering.plaintext.extension

import com.layerdocs.core.context.Context
import com.layerdocs.core.flavor.RendererFactory
import com.layerdocs.core.rendering.RenderingComponents
import com.layerdocs.rendering.plaintext.node.PlainTextNodeRenderer
import com.layerdocs.rendering.plaintext.post.PlainTextPostRenderer

/**
 * The plain-text rendering plug-in produces a plain-text representation of the document.
 * It can be used for generating text-only versions of documents for accessibility or
 * for further processing by text-based tools.
 */
@Suppress("UnusedReceiverParameter")
fun RendererFactory.plainText(context: Context) =
    RenderingComponents(
        nodeRenderer = PlainTextNodeRenderer(context),
        postRenderer = PlainTextPostRenderer(context),
    )
