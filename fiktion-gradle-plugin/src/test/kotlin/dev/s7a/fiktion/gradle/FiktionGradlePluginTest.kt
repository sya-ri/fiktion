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

    @Test
    fun `plugin auto-loads add-ons from test fixtures dependencies`() {
        val repository = Path.of(System.getProperty("user.dir")).parent
        val directory = Files.createTempDirectory("fiktion-test-fixtures-test")
        directory.resolve("settings.gradle.kts").writeText(
            """
            pluginManagement {
                includeBuild("${repository.toString().replace("\\", "\\\\")}")
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                }
            }

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }

            rootProject.name = "fiktion-test-fixtures-test"
            include(":my-lib", ":my-app")
            includeBuild("${repository.toString().replace("\\", "\\\\")}")
            """.trimIndent(),
        )
        directory.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm") version "2.3.21" apply false
                id("dev.s7a.fiktion") apply false
            }
            """.trimIndent(),
        )

        val lib = directory.resolve("my-lib")
        Files.createDirectories(lib.resolve("src/main/kotlin/com/example/mylib"))
        Files.createDirectories(lib.resolve("src/testFixtures/kotlin/com/example/mylib"))
        Files.createDirectories(lib.resolve("src/testFixtures/resources/META-INF/fiktion"))
        lib.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm")
                `java-test-fixtures`
            }

            dependencies {
                testFixturesApi("dev.s7a:fiktion-core:0.2.3")
            }
            """.trimIndent(),
        )
        lib.resolve("src/main/kotlin/com/example/mylib/Token.kt").writeText(
            """
            package com.example.mylib

            @JvmInline
            value class Token(val value: String)
            """.trimIndent(),
        )
        lib.resolve("src/testFixtures/kotlin/com/example/mylib/MyLibFiktionAddon.kt").writeText(
            """
            package com.example.mylib

            import dev.s7a.fiktion.FiktionAddon
            import dev.s7a.fiktion.FiktionAddonBuilder
            import dev.s7a.fiktion.generates

            object MyLibFiktionAddon : FiktionAddon {
                override val id: String = "my-lib"

                override fun install(builder: FiktionAddonBuilder) {
                    with(builder) {
                        type<Token>() generates Token("from-addon")
                    }
                }
            }
            """.trimIndent(),
        )
        lib.resolve("src/testFixtures/resources/META-INF/fiktion/addons").writeText(
            "com.example.mylib.MyLibFiktionAddon",
        )

        val app = directory.resolve("my-app")
        Files.createDirectories(app.resolve("src/test/kotlin/com/example/myapp"))
        app.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm")
                id("dev.s7a.fiktion")
            }

            dependencies {
                implementation(project(":my-lib"))
                testImplementation(testFixtures(project(":my-lib")))
                testImplementation("dev.s7a:fiktion-core:0.2.3")
                testImplementation(kotlin("test"))
            }
            """.trimIndent(),
        )
        app.resolve("src/test/kotlin/com/example/myapp/TokenTest.kt").writeText(
            """
            package com.example.myapp

            import com.example.mylib.Token
            import dev.s7a.fiktion.fake
            import kotlin.test.Test
            import kotlin.test.assertEquals

            class TokenTest {
                @Test
                fun fakeTokenUsesTestFixturesAddon() {
                    assertEquals(Token("from-addon"), fake<Token>(seed = 123))
                }
            }
            """.trimIndent(),
        )

        val result =
            GradleRunner
                .create()
                .withProjectDir(directory.toFile())
                .withArguments(":my-app:test", "--stacktrace")
                .build()

        assertEquals(SUCCESS, result.task(":my-app:test")?.outcome)
    }

    @Test
    fun `plugin auto-loads add-ons from local jar dependencies`() {
        val repository = Path.of(System.getProperty("user.dir")).parent
        val directory = Files.createTempDirectory("fiktion-local-jar-addon-test")
        directory.resolve("settings.gradle.kts").writeText(
            """
            pluginManagement {
                includeBuild("${repository.toString().replace("\\", "\\\\")}")
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                }
            }

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }

            rootProject.name = "fiktion-local-jar-addon-test"
            include(":my-addon", ":my-app")
            includeBuild("${repository.toString().replace("\\", "\\\\")}")
            """.trimIndent(),
        )
        directory.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm") version "2.3.21" apply false
                id("dev.s7a.fiktion") apply false
            }
            """.trimIndent(),
        )

        val addon = directory.resolve("my-addon")
        Files.createDirectories(addon.resolve("src/main/kotlin/com/example/addon"))
        Files.createDirectories(addon.resolve("src/main/resources/META-INF/fiktion"))
        addon.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm")
            }

            dependencies {
                compileOnly("dev.s7a:fiktion-core:0.2.3")
            }
            """.trimIndent(),
        )
        addon.resolve("src/main/kotlin/com/example/addon/Token.kt").writeText(
            """
            package com.example.addon

            @JvmInline
            value class Token(val value: String)
            """.trimIndent(),
        )
        addon.resolve("src/main/kotlin/com/example/addon/LocalJarFiktionAddon.kt").writeText(
            """
            package com.example.addon

            import dev.s7a.fiktion.FiktionAddon
            import dev.s7a.fiktion.FiktionAddonBuilder
            import dev.s7a.fiktion.generates

            object LocalJarFiktionAddon : FiktionAddon {
                override val id: String = "local-jar"

                override fun install(builder: FiktionAddonBuilder) {
                    with(builder) {
                        type<Token>() generates Token("from-local-jar")
                    }
                }
            }
            """.trimIndent(),
        )
        addon.resolve("src/main/resources/META-INF/fiktion/addons").writeText(
            "com.example.addon.LocalJarFiktionAddon",
        )

        val app = directory.resolve("my-app")
        Files.createDirectories(app.resolve("src/test/kotlin/com/example/myapp"))
        app.resolve("build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm")
                id("dev.s7a.fiktion")
            }

            dependencies {
                testImplementation(files("../my-addon/build/libs/my-addon.jar"))
                testImplementation("dev.s7a:fiktion-core:0.2.3")
                testImplementation(kotlin("test"))
            }
            """.trimIndent(),
        )
        app.resolve("src/test/kotlin/com/example/myapp/TokenTest.kt").writeText(
            """
            package com.example.myapp

            import com.example.addon.Token
            import dev.s7a.fiktion.fake
            import kotlin.test.Test
            import kotlin.test.assertEquals

            class TokenTest {
                @Test
                fun fakeTokenUsesLocalJarAddon() {
                    assertEquals(Token("from-local-jar"), fake<Token>(seed = 123))
                }
            }
            """.trimIndent(),
        )

        val jarResult =
            GradleRunner
                .create()
                .withProjectDir(directory.toFile())
                .withArguments(":my-addon:jar", "--stacktrace")
                .build()
        val testResult =
            GradleRunner
                .create()
                .withProjectDir(directory.toFile())
                .withArguments(":my-app:test", "--stacktrace")
                .build()

        assertEquals(SUCCESS, jarResult.task(":my-addon:jar")?.outcome)
        assertEquals(SUCCESS, testResult.task(":my-app:test")?.outcome)
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
