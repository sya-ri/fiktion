package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ForbiddenGeneratorCallTest {
    private val rule = ForbiddenGeneratorCall(Config.empty)

    @Test
    fun `reports forbidden calls in generator lambdas`() {
        val findings =
            rule.lint(
                """
                import java.io.File
                import java.nio.file.Files
                import java.time.Instant
                import java.time.LocalDate
                import java.time.LocalDateTime
                import java.util.Date
                import java.util.UUID
                import kotlin.random.Random

                fun configure() {
                    type<User>() generatesBy {
                        assertTrue(true)
                        User()
                    }
                    type<String>().generatesBy {
                        kotlin.test.assertEquals("value", "value")
                        "value"
                    }
                    type<Instant>() generatesBy { Clock.System.now() }
                    type<Instant>() generatesBy { kotlinx.datetime.Clock.System.now() }
                    type<Instant>() generatesBy { Instant.now() }
                    type<Instant>() generatesBy { LocalDate.now() }
                    type<Instant>() generatesBy { LocalDateTime.now() }
                    type<Instant>() generatesBy { Instant.now() }
                    type<Instant>() generatesBy { LocalDate.now() }
                    type<Instant>() generatesBy { LocalDateTime.now() }
                    type<Instant>() generatesBy { kotlin.time.Clock.System.now() }
                    type<Instant>() generatesBy { kotlin.time.Instant.now() }
                    type<Instant>().generatesBy { Clock.System.now() }
                    type<Long>() generatesBy { System.currentTimeMillis() }
                    type<Long>() generatesBy { System.nanoTime() }
                    type<String>() generatesBy { System.getenv("HOME") }
                    type<String>() generatesBy { System.getProperty("user.home") }
                    type<Long>() generatesBy { System.currentTimeMillis() }
                    type<Long>() generatesBy { System.nanoTime() }
                    type<String>() generatesBy { System.getenv("HOME") }
                    type<String>() generatesBy { System.getProperty("user.home") }
                    type<Unit>() generatesBy { Thread.sleep(1) }
                    type<Unit>() generatesBy { Thread.sleep(1) }
                    type<Date>() generatesBy { Date() }
                    type<Date>() generatesBy { Date() }
                    type<UUID>() generatesBy { UUID.randomUUID() }
                    type<UUID>() generatesBy { UUID.randomUUID() }
                    type<String>() generatesBy { File("fixture.txt").readText() }
                    type<ByteArray>() generatesBy { File("fixture.txt").readBytes() }
                    type<String>() generatesBy { Files.readString(path) }
                    type<ByteArray>() generatesBy { Files.readAllBytes(path) }
                    type<ProcessBuilder>() generatesBy { ProcessBuilder("echo", "value") }
                    type<Int>() generatesBy { Random.nextInt() }
                    type<Long>() generatesBy { kotlin.random.Random.nextLong() }
                    type<ByteArray>() generatesBy { Random.nextBytes(4) }
                    type<Runtime>() generatesBy { Runtime.getRuntime() }
                }
                """.trimIndent(),
            )

        assertEquals(36, findings.size)
        val messages = findings.map { finding -> finding.message }
        assertTrue("Do not call `assertTrue` inside a Fiktion generator." in messages)
        assertTrue("Do not call `assertEquals` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Clock.System.now` inside a Fiktion generator." in messages)
        assertTrue("Do not call `System.currentTimeMillis` inside a Fiktion generator." in messages)
        assertTrue("Do not call `System.nanoTime` inside a Fiktion generator." in messages)
        assertTrue("Do not call `System.getenv` inside a Fiktion generator." in messages)
        assertTrue("Do not call `System.getProperty` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Thread.sleep` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Date` inside a Fiktion generator." in messages)
        assertTrue("Do not call `UUID.randomUUID` inside a Fiktion generator." in messages)
        assertTrue("Do not call `File.readText` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Files.readString` inside a Fiktion generator." in messages)
        assertTrue("Do not call `ProcessBuilder` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Random.nextInt` inside a Fiktion generator." in messages)
        assertTrue("Do not call `kotlin.random.Random.nextLong` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Random.nextBytes` inside a Fiktion generator." in messages)
        assertTrue("Do not call `Runtime.getRuntime` inside a Fiktion generator." in messages)
    }

    @Test
    fun `does not report calls outside generator lambdas`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    val user = fake<User>()
                    assertTrue(user.id.isNotBlank())
                }

                fun configure() {
                    val value = Clock.System.now()
                    type<Instant>() generates value
                    type<Instant>() generatesBy { fixedClock.now() }
                    type<Instant>() generatesBy { clock.now() }
                    type<Instant>() generatesBy { user.now() }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured calls`() {
        val findings =
            ForbiddenGeneratorCall(
                TestConfig(
                    "calls" to
                        listOf(
                            "expectThat",
                            "shouldBe",
                            "TestClock.now",
                            "TestSystem.currentTimeMillis",
                        ),
                ),
            ).lint(
                """
                fun configure() {
                    type<User>() generatesBy {
                        expectThat(true)
                        User()
                    }
                    type<User>() generatesBy {
                        assertTrue(true)
                        User()
                    }
                    type<User>() generatesBy {
                        User() shouldBe User()
                    }
                    type<Instant>() generatesBy { Clock.System.now() }
                    type<Instant>() generatesBy { TestClock.now() }
                    type<Long>() generatesBy { System.currentTimeMillis() }
                    type<Long>() generatesBy { TestSystem.currentTimeMillis() }
                }
                """.trimIndent(),
            )

        assertEquals(4, findings.size)
        assertEquals("Do not call `expectThat` inside a Fiktion generator.", findings[0].message)
        assertEquals("Do not call `shouldBe` inside a Fiktion generator.", findings[1].message)
        assertEquals("Do not call `TestClock.now` inside a Fiktion generator.", findings[2].message)
        assertEquals("Do not call `TestSystem.currentTimeMillis` inside a Fiktion generator.", findings[3].message)
    }

    @Test
    fun `does not autocorrect forbidden generator calls`() {
        val code =
            """
            fun configure() {
                type<User>() generatesBy {
                    assertTrue(true)
                    User()
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = ForbiddenGeneratorCall(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
