package com.layerdocs.cli

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the `--version` option of the CLI.
 */
class VersionTest {
    @Test
    fun `version echo`() {
        val output = LayerDocsCommand().test("--version").output
        assertTrue(Regex("layerdocs version \\d+\\.\\d+\\.\\d+").containsMatchIn(output))
    }
}
