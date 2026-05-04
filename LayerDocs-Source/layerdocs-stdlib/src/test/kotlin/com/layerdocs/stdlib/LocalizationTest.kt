package com.layerdocs.stdlib

import com.layerdocs.core.context.MutableContext
import com.layerdocs.core.flavor.layerdocs.LayerDocsFlavor
import com.layerdocs.core.function.value.DictionaryValue
import com.layerdocs.core.function.value.wrappedAsValue
import com.layerdocs.core.localization.LocaleLoader
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [Localization] module tests.
 */
class LocalizationTest {
    private val context = MutableContext(LayerDocsFlavor)

    @BeforeTest
    fun setup() {
        context.documentInfo = context.documentInfo.copy(locale = LocaleLoader.SYSTEM.fromName("English")!!)
    }

    @Test
    fun `localization table`() {
        localization(
            context,
            "mytable",
            contents =
                mapOf(
                    "English" to
                        DictionaryValue(
                            mutableMapOf(
                                "morning" to "Good morning".wrappedAsValue(),
                                "evening" to "Good evening".wrappedAsValue(),
                            ),
                        ),
                    "Italian" to
                        DictionaryValue(
                            mutableMapOf(
                                "morning" to "Buongiorno".wrappedAsValue(),
                                "evening" to "Buonasera".wrappedAsValue(),
                            ),
                        ),
                ),
        )

        assertEquals(1, context.localizationTables.size)
        val table = context.localizationTables["mytable"]!!

        assertEquals("Good morning", table[LocaleLoader.SYSTEM.fromName("English")!!]!!["morning"])
        assertEquals("Buongiorno", table[LocaleLoader.SYSTEM.fromName("Italian")!!]!!["morning"])

        assertEquals("Good evening", context.localize("mytable", "evening"))
    }
}
