package com.layerdocs.interaction

import com.layerdocs.interaction.executable.NodeJsWrapper
import com.layerdocs.interaction.executable.NodeModule
import com.layerdocs.interaction.executable.NpmWrapper
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private fun npm() = NpmWrapper(NpmWrapper.defaultPath)

private fun node(workingDirectory: File) = NodeJsWrapper(NodeJsWrapper.defaultPath, workingDirectory)

/**
 * Tests for wrappers around Node.js and NPM.
 * @see NodeJsWrapper
 * @see NpmWrapper
 */
class NodeNpmWrapperTest {
    private data object PuppeteerNodeModule : NodeModule("puppeteer")

    private val directory: File =
        createTempDirectory()
            .toFile()

    @BeforeTest
    fun setup() {
        directory.deleteRecursively()
        directory.mkdirs()
    }

    @Test
    fun `nodejs wrapper`() {
        val node = node(workingDirectory = directory)
        assumeTrue(node.isValid)

        assertEquals("Hello, LayerDocs!", node.eval("console.log('Hello, LayerDocs!')"))
        assertEquals(
            "Hello, LayerDocs!\nHello, LayerDocs!",
            node.eval(
                """
                function hello() {
                    console.log('Hello, LayerDocs!');
                }
                hello();
                hello();
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun `nonexisting nodejs`() {
        val node = NodeJsWrapper("layerdocs-nodejs-nonexisting-path", directory)
        assertEquals(false, node.isValid)
    }

    @Test
    fun `npm wrapper`() {
        assumeTrue(npm().isValid)
    }

    @Test
    fun `nonexisting npm`() {
        val npm = NpmWrapper("layerdocs-npm-nonexisting-path")
        assertEquals(false, npm.isValid)
    }

    @Test
    fun `nonexisting module not installed`() {
        val node = node(workingDirectory = directory)
        val npm = npm()
        val module = NodeModule("layerdocs-nonexisting-module-xyz")
        assumeTrue(npm.isValid)
        assertFalse(npm.isInstalled(node, module))
    }
}
