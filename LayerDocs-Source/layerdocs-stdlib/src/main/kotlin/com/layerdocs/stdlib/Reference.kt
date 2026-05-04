package com.layerdocs.stdlib

import com.layerdocs.core.ast.layerdocs.reference.CrossReference
import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.library.module.moduleOf
import com.layerdocs.core.function.reflect.annotation.Name
import com.layerdocs.core.function.value.wrappedAsValue

/**
 * `Reference` stdlib module exporter.
 * This module handles cross-references.
 * @see com.layerdocs.core.ast.layerdocs.reference
 */
val Reference: LayerDocsModule =
    moduleOf(
        ::reference,
    )

/**
 * Creates a reference to a target node with a matching ID.
 *
 * Examples of referenceable nodes include:
 *
 * - Headings
 *
 *   ```markdown
 *   # Heading {#id}
 *   ```
 *
 * - Figures
 *
 *   ```markdown
 *   ![Alt](image.png "Caption"){#id}
 *   ```
 *
 * - Tables
 *
 *   ```markdown
 *   | Header | Header |
 *   |--------|--------|
 *   | Cell   | Cell   |
 *   {#id}
 *   ```
 *
 * - Code blocks
 *
 *   ~~~markdown
 *   ```python {#id}
 *   print("Hello, World!")
 *   ```
 *   ~~~
 *
 * - Custom [numbered] blocks
 *
 *   ```markdown
 *   .numbered {key} ref:{id}
 *   ```
 *
 * The reference is successfully resolved if the ID matches that of a referenceable node in the document:
 *
 * ```
 * .ref {id}
 * ```
 *
 * @param id the reference ID of the target node being referenced
 * @return a [CrossReference] to the target node
 * @wiki cross-references
 */
@Name("ref")
fun reference(id: String) = CrossReference(id).wrappedAsValue()
