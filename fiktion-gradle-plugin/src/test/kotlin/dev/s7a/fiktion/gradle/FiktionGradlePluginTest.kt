package dev.s7a.fiktion.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

/**
 * Functional tests for applying the Fiktion Gradle plugin.
 */
class FiktionGradlePluginTest {
    @Test
    fun `plugin registers the fiktion extension with defaults`() {
        val result =
            testProject(
                buildScript =
                    """
                    plugins {
                        id("dev.s7a.fiktion")
                    }

                    registerPrintExtensionTask()
                    """.trimIndent(),
            ).run("printFiktionExtension")

        assertEquals(SUCCESS, result.task(":printFiktionExtension")?.outcome)
        assertContains(result.output, "enabled=false")
        assertContains(result.output, "testEnabled=true")
    }

    @Test
    fun `plugin accepts source set overrides in the fiktion extension`() {
        val result =
            testProject(
                buildScript =
                    """
                    plugins {
                        id("dev.s7a.fiktion")
                    }

                    fiktion {
                        sourceSet("commonMain") {
                            enabled.set(false)
                        }
                        sourceSet("commonTest") {
                            enabled.set(true)
                        }
                    }

                    registerPrintExtensionTask()
                    """.trimIndent(),
            ).run("printFiktionExtension")

        assertEquals(SUCCESS, result.task(":printFiktionExtension")?.outcome)
        assertContains(result.output, "commonMain=false")
        assertContains(result.output, "commonTest=true")
    }

    /**
     * Creates a temporary Gradle project for a TestKit run.
     */
    private fun testProject(buildScript: String): TestProject {
        val directory = Files.createTempDirectory("fiktion-gradle-plugin-test")
        directory.resolve("settings.gradle.kts").writeText("""rootProject.name = "fiktion-test"""")
        directory.resolve("build.gradle.kts").writeText(
            """
            import dev.s7a.fiktion.gradle.FiktionExtension

            $buildScript

            fun registerPrintExtensionTask() {
                tasks.register("printFiktionExtension") {
                    doLast {
                        val extension = project.extensions.getByType(FiktionExtension::class.java)
                        println("enabled=${'$'}{extension.enabled.get()}")
                        println("testEnabled=${'$'}{extension.testEnabled.get()}")
                        extension.sourceSets.forEach { sourceSet ->
                            println("${'$'}{sourceSet.name}=${'$'}{sourceSet.enabled.get()}")
                        }
                    }
                }
            }
            """.trimIndent(),
        )
        return TestProject(directory)
    }

    /**
     * Gradle project fixture used by the functional tests.
     */
    private class TestProject(
        private val directory: Path,
    ) {
        /**
         * Runs Gradle with the plugin-under-test classpath.
         */
        fun run(vararg arguments: String) =
            GradleRunner
                .create()
                .withProjectDir(directory.toFile())
                .withArguments(*arguments, "--stacktrace")
                .withPluginClasspath()
                .build()
    }
}
