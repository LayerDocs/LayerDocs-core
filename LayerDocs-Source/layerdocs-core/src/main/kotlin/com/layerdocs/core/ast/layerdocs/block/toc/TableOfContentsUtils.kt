package com.layerdocs.core.ast.layerdocs.block.toc

import com.layerdocs.core.ast.attributes.location.LocationTrackableNode
import com.layerdocs.core.ast.base.block.Paragraph
import com.layerdocs.core.ast.base.block.list.ListItem
import com.layerdocs.core.ast.base.block.list.OrderedList
import com.layerdocs.core.ast.base.inline.Link
import com.layerdocs.core.ast.layerdocs.block.list.FocusListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.LocationTargetListItemVariant
import com.layerdocs.core.ast.layerdocs.block.list.TableOfContentsItemVariant
import com.layerdocs.core.context.toc.TableOfContents
import com.layerdocs.core.document.numbering.DocumentNumbering
import com.layerdocs.core.util.node.stripRichContent
import com.layerdocs.core.visitor.node.NodeVisitor

/**
 * Filters [TableOfContents.Item]s based on the given [TableOfContentsView]'s configuration:
 * items that exceed the maximum depth are filtered out.
 * @returns a sequence of filtered [TableOfContents.Item]s.
 */
private fun filterTableOfContentsItems(
    view: TableOfContentsView,
    items: List<TableOfContents.Item>,
): Sequence<TableOfContents.Item> =
    items
        .asSequence()
        .filter { it.depth <= view.maxDepth }

/**
 * Converts a table of contents to a renderable [OrderedList].
 * @param renderer renderer to use to render items
 * @param items ToC items [view] contains, prior to filtering
 * @param loose whether the list should be rendered in loose mode
 * @param wrapLinksInParagraphs whether to wrap the links in paragraphs
 * @param linkUrlMapper function that obtains the URL to send to when a ToC item is interacted with
 */
fun convertTableOfContentsToListNode(
    view: TableOfContentsView,
    renderer: NodeVisitor<CharSequence>,
    items: List<TableOfContents.Item>,
    loose: Boolean = true,
    wrapLinksInParagraphs: Boolean = false,
    linkUrlMapper: (TableOfContents.Item) -> String,
): OrderedList {
    // Gets the content of an inner (nested, level 2+ headings) ToC item.
    fun getNestedItemContent(item: TableOfContents.Item) =
        listOfNotNull(
            Link(
                // Rich content of the heading is ignored in the ToC entry.
                item.text.stripRichContent(renderer),
                url = linkUrlMapper(item),
                title = null,
            ).let {
                if (wrapLinksInParagraphs) Paragraph(listOf(it)) else it
            },
            // Recursively include sub-items.
            filterTableOfContentsItems(view, item.subItems)
                .takeIf { it.any() }
                ?.let { convertTableOfContentsToListNode(view, renderer, it.toList(), loose, wrapLinksInParagraphs, linkUrlMapper) },
        )

    // Level 1 headings.
    return OrderedList(
        startIndex = 1,
        isLoose = loose,
        children =
            filterTableOfContentsItems(view, items)
                .map {
                    ListItem(
                        children = getNestedItemContent(it),
                        variants =
                            buildList {
                                this += TableOfContentsItemVariant(it)

                                // When at least one item is focused, the other items are less visible.
                                this += FocusListItemVariant(isFocused = view.hasFocus(it))

                                // If the target node's location can be tracked,
                                // the list item displays its location.
                                // Since the targets are usually headings, thus location trackable, this is applied.
                                // Only add location variant for numbered headings.
                                if (it.target is LocationTrackableNode && it.target.canTrackLocation) {
                                    this += LocationTargetListItemVariant(it.target, DocumentNumbering::headings)
                                }
                            },
                    )
                }.toList(),
    )
}
