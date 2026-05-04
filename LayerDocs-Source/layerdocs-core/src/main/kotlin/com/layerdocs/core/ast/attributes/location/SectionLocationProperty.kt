package com.layerdocs.core.ast.attributes.location

import com.layerdocs.core.property.Property

/**
 * [Property] that is assigned to each node that requests its location to be tracked ([LocationTrackableNode]).
 * It contains the node's location in the document, in terms of section indices.
 * @see SectionLocation
 * @see com.layerdocs.core.context.hooks.location.LocationAwarenessHook for the storing stage
 */
data class SectionLocationProperty(
    override val value: SectionLocation,
) : Property<SectionLocation> {
    companion object : Property.Key<SectionLocation>

    override val key = SectionLocationProperty
}
