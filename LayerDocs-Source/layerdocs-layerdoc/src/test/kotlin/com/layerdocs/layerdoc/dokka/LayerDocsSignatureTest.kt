package com.layerdocs.layerdoc.dokka

import com.layerdocs.core.function.library.module.LayerDocsModule
import com.layerdocs.core.function.value.DynamicValue
import com.layerdocs.core.function.value.VoidValue
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for LayerDocs signatures.
 */
class LayerDocsSignatureTest :
    LayerDocDokkaTest(
        imports = listOf(LayerDocsModule::class, VoidValue::class, DynamicValue::class),
        stringImports = listOf(LayerDocsModule::class.java.packageName + ".*"),
    ) {
    /**
     * @param functionCode the code of the function to test. Its name must be equal to [functionName]
     * @param functionName the name of the function to test
     * @param block the block to execute with the signature as a parameter
     */
    private fun testSignature(
        functionCode: String,
        functionName: String = "func",
        block: (String) -> Unit,
    ) {
        val sources =
            mapOf(
                "TestModule.kt" to
                    """
                    val TestModule: LayerDocsModule = moduleOf(::$functionName)
                    $functionCode
                    """.trimIndent(),
            )

        test(
            sources,
            outModule = "TestModule",
            outName = functionName,
        ) { block(getSignature(it)) }
    }

    @Test
    fun `no parameters`() {
        testSignature("fun func() = VoidValue") {
            assertEquals(".func -> Void", it)
        }
    }

    @Test
    fun `one parameter`() {
        testSignature("fun func(a: Int) = VoidValue") {
            assertEquals(".func a:{Int} -> Void", it)
        }
    }

    @Test
    fun `two parameters`() {
        testSignature("fun func(a: Int, b: Iterable<DynamicValue>) = VoidValue") {
            assertEquals(".func a:{Int} b:{Iterable<Dynamic>} -> Void", it)
        }
    }

    @Test
    fun `default value`() {
        testSignature("fun func(a: Int = 0) = VoidValue") {
            assertEquals(".func a:{Int = 0} -> Void", it)
        }
    }

    @Test
    fun `line breaking, same length`() {
        testSignature("fun func(a: Int, b: Int, c: Int) = VoidValue") {
            assertEquals(
                """
                .func a:{Int} \
                      b:{Int} \
                      c:{Int}
                -> Void
                """.trimIndent(),
                it,
            )
        }
    }

    @Test
    fun `line breaking, different length`() {
        testSignature("fun func(abcd: Int, ef: String, ghijkl: Int) = VoidValue") {
            assertEquals(
                """
                .func abcd:{Int} \
                        ef:{String} \
                    ghijkl:{Int}
                -> Void
                """.trimIndent(),
                it,
            )
        }
    }

    @Test
    fun `line breaking, different length, out of bounds`() {
        testSignature("fun func(abcd: Int, ef: String, ghijklmnopqrst: Int) = VoidValue") {
            assertEquals(
                """
                .func      abcd:{Int} \
                             ef:{String} \
                 ghijklmnopqrst:{Int}
                -> Void
                """.trimIndent(),
                it,
            )
        }
    }
}
