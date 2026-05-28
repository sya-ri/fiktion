package dev.s7a.fiktion.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
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

    @Test
    fun `plugin reads automatic add-on indexes from class directories`() {
        val directory = Files.createTempDirectory("fiktion-addon-index-directory")
        directory
            .resolve("META-INF")
            .resolve("fiktion")
            .also { path -> Files.createDirectories(path) }
            .resolve("addons")
            .writeText(
                """
                com.example.FirstAddon
                # comment
                com.example.SecondAddon # trailing comment
                """.trimIndent(),
            )

        assertContentEquals(
            listOf("com.example.FirstAddon", "com.example.SecondAddon"),
            directory.toFile().fiktionAddonClassNames(),
        )
    }

    @Test
    fun `plugin reads automatic add-on indexes from jars`() {
        val jar = Files.createTempFile("fiktion-addon-index", ".jar")
        ZipOutputStream(Files.newOutputStream(jar)).use { zip ->
            zip.putNextEntry(ZipEntry("META-INF/fiktion/addons"))
            zip.write(
                """
                com.example.FirstAddon
                com.example.SecondAddon
                """.trimIndent().toByteArray(),
            )
            zip.closeEntry()
        }

        assertContentEquals(
            listOf("com.example.FirstAddon", "com.example.SecondAddon"),
            jar.toFile().fiktionAddonClassNames(),
        )
    }

    @Test
    fun `plugin reads automatic add-on indexes from klibs`() {
        val klib = Files.createTempFile("fiktion-addon-index", ".klib")
        ZipOutputStream(Files.newOutputStream(klib)).use { zip ->
            zip.putNextEntry(ZipEntry("default/resources/META-INF/fiktion/addons"))
            zip.write("com.example.CommonAddon".toByteArray())
            zip.closeEntry()
        }

        assertContentEquals(
            listOf("com.example.CommonAddon"),
            klib.toFile().fiktionAddonClassNames(),
        )
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
