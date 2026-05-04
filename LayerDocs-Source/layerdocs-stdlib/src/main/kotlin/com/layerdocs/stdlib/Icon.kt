package com.layerdocs.stdlib

import com.layerdocs.core.ast.layerdocs.inline.IconImage
import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.library.module.moduleOf
import com.layerdocs.core.function.value.wrappedAsValue

/**
 * `Icon` stdlib module exporter.
 * This module handles pixel-perfect icons.
 */
val Icon: LayerDocsModule =
    moduleOf(
        ::icon,
    )

/**
 * Shows a pixel-perfect icon, looked up from the icon library by its name.
 *
 * Note: icon libraries and names are dependent on the renderer.
 * No validation is performed at compile time, and missing icons may not be rendered or rendered incorrectly.
 *
 * In HTML (and HTML-PDF) rendering, the [Bootstrap Icons](https://icons.getbootstrap.com/#icons) library is used.
 *
 * @param name the name of the icon
 */
fun icon(name: String) = IconImage(name).wrappedAsValue()
