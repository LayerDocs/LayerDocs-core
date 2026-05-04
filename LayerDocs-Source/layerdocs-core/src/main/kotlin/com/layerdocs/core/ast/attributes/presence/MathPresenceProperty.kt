package com.layerdocs.core.ast.attributes.presence

import com.layerdocs.core.ast.attributes.AstAttributes
import com.layerdocs.core.ast.attributes.MutableAstAttributes
import com.layerdocs.core.property.Property

/**
 * If this property is present in [com.layerdocs.core.ast.attributes.AstAttributes.thirdPartyPresenceProperties]
 * and its [value] is true, it means there is at least one math block or inline in the AST.
 * This is used to load the KaTeX library in HTML rendering only if necessary.
 * @see com.layerdocs.core.context.hooks.presence.MathPresenceHook
 */
data class MathPresenceProperty(
    override val value: Boolean,
) : Property<Boolean> {
    companion object : Property.Key<Boolean>

    override val key: Property.Key<Boolean> = MathPresenceProperty
}

/**
 * Whether there is at least one math block or inline in the AST.
 * @see MathPresenceProperty
 */
val AstAttributes.hasMath: Boolean
    get() = hasThirdParty(MathPresenceProperty)

/**
 * Marks the presence of math blocks or inlines in the AST
 * if at least one math element is present in the document.
 * @see MathPresenceProperty
 * @see com.layerdocs.core.context.hooks.presence.MathPresenceHook
 */
fun MutableAstAttributes.markMathPresence() = markThirdPartyPresence(MathPresenceProperty(true))
