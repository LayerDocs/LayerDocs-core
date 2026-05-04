package com.layerdocs.layerdoc.dokka

import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.value.VoidValue
import com.layerdocs.layerdoc.dokka.transformers.module.LayerDocsModulesStorage
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

/**
 * Tests for synthetic module creation in Dokka.
 */
class ModuleTransformerTest :
    LayerDocDokkaTest(
        imports = listOf(LayerDocsModule::class, VoidValue::class),
        stringImports = listOf(LayerDocsModule::class.java.packageName + ".*"),
    ) {
    @Test
    fun `two modules`() {
        val sources =
            mapOf(
                "M1.kt" to
                    """
                    val Module1: LayerDocsModule = moduleOf(::aFunction)
                    fun aFunction() = VoidValue
                    """.trimIndent(),
                "M2.kt" to
                    """
                    val Module2: LayerDocsModule = moduleOf(::bFunction)
                    fun bFunction() = VoidValue
                    """.trimIndent(),
            )

        test(
            sources,
            outModule = "Module1",
            outName = "a-function",
        ) {
            assertEquals(2, LayerDocsModulesStorage.moduleCount)
            assertContains(getSignature(it), "aFunction")
        }
    }

    @Test
    fun `two modules and leftover file`() {
        val sources =
            mapOf(
                "M1.kt" to
                    """
                    val Module1: LayerDocsModule = moduleOf(::aFunction)
                    fun aFunction() = VoidValue
                    """.trimIndent(),
                "M2.kt" to
                    """
                    val Module2: LayerDocsModule = moduleOf(::bFunction)
                    fun bFunction() = VoidValue
                    """.trimIndent(),
                "leftover.kt" to "object leftover {}",
            )

        test(
            sources,
            outName = "leftover/index",
        ) {
            // No error = file exists
        }
    }

    @Test
    fun `five modules, package list`() {
        val moduleCount = 5
        val sources =
            (1..moduleCount).associate {
                "M$it.kt" to
                    """
                    val Module$it: LayerDocsModule = moduleOf(::someFunction$it)
                    fun someFunction$it() = VoidValue
                    """.trimIndent()
            }

        test(
            sources,
            outName = "root/package-list",
            autoPath = false,
        ) {
            assertEquals(moduleCount, LayerDocsModulesStorage.moduleCount)
            assertContains(it, rootPackage)
            for (i in 1..moduleCount) {
                assertContains(it, "$rootPackage.module.Module$i")
                assertContains(it, "$rootPackage/-module$i.html")
                assertContains(it, "$rootPackage.module.Module$i/some-function$i.html")
            }
        }
    }
}
