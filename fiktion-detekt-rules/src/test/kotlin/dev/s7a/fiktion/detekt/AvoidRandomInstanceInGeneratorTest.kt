package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidRandomInstanceInGeneratorTest {
    @Test
    fun `reports random instances in generator lambdas`() {
        val findings =
            AvoidRandomInstanceInGenerator(Config.empty).lint(
                """
                fun configure() {
                    type<Int>() generatesBy { kotlin.random.Random.nextInt() }
                    type<Double>() generatesBy { Random.Default.nextDouble() }
                    type<Long>() generatesBy { Random(1).nextLong() }
                    type<Long>() generatesBy { kotlin.random.Random(1).nextLong() }
                    type<Random>() generatesBy { Random.Default }
                    type<Random>() generatesBy { Random(1) }
                }
                """.trimIndent(),
            )

        assertEquals(6, findings.size)
        assertEquals(
            "Use `random` instead of `kotlin.random.Random` inside a Fiktion generator.",
            findings[0].message,
        )
    }

    @Test
    fun `auto corrects random instances`() {
        val ktFile =
            compileContentForTest(
                """
                fun configure() {
                    type<Int>() generatesBy { kotlin.random.Random.nextInt() }
                    type<Double>() generatesBy { Random.Default.nextDouble() }
                    type<Long>() generatesBy { Random(1).nextLong() }
                    type<Long>() generatesBy { kotlin.random.Random(1).nextLong() }
                    type<Random>() generatesBy { Random.Default }
                    type<Random>() generatesBy { Random(1) }
                }
                """.trimIndent(),
            )

        val findings = AvoidRandomInstanceInGenerator(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(6, findings.size)
        assertEquals(
            """
            fun configure() {
                type<Int>() generatesBy { random.nextInt() }
                type<Double>() generatesBy { random.nextDouble() }
                type<Long>() generatesBy { random.nextLong() }
                type<Long>() generatesBy { random.nextLong() }
                type<Random>() generatesBy { random }
                type<Random>() generatesBy { random }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
