package dev.s7a.fiktion.gradle

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for source set selection used by the Gradle plugin.
 */
class FiktionSourceSetSelectionTest {
    @Test
    fun `test source sets are enabled by default`() {
        val extension = fiktionExtension()

        assertTrue(extension.isEnabledFor("test"))
        assertTrue(extension.isEnabledFor("commonTest"))
        assertTrue(extension.isEnabledFor("jvmTest"))
    }

    @Test
    fun `main source sets are disabled by default`() {
        val extension = fiktionExtension()

        assertFalse(extension.isEnabledFor("commonMain"))
        assertFalse(extension.isEnabledFor("jvmMain"))
    }

    @Test
    fun `global enabled applies to every source set`() {
        val extension = fiktionExtension()

        extension.enabled.set(true)

        assertTrue(extension.isEnabledFor("commonMain"))
        assertTrue(extension.isEnabledFor("commonTest"))
    }

    @Test
    fun `test enabled disables default test source set support`() {
        val extension = fiktionExtension()

        extension.testEnabled.set(false)

        assertFalse(extension.isEnabledFor("test"))
        assertFalse(extension.isEnabledFor("commonTest"))
        assertFalse(extension.isEnabledFor("jvmTest"))
    }

    @Test
    fun `source set overrides take precedence over global settings`() {
        val extension = fiktionExtension()

        extension.enabled.set(true)
        extension.sourceSet("commonMain") { sourceSet ->
            sourceSet.enabled.set(false)
        }
        extension.sourceSet("customMain") { sourceSet ->
            sourceSet.enabled.set(true)
        }

        assertFalse(extension.isEnabledFor("commonMain"))
        assertTrue(extension.isEnabledFor("customMain"))
    }

    /**
     * Creates a Fiktion extension backed by a Gradle test project.
     */
    private fun fiktionExtension(): FiktionExtension {
        val project = ProjectBuilder.builder().build()
        return project.objects.newInstance(FiktionExtension::class.java)
    }
}
