package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferFixedDefaultProbabilityTest {
    private val rule = PreferFixedDefaultProbability(Config.empty)

    @Test
    fun `reports fixed default probabilities`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<Profile>() generates Profile("value") orDefaultAt 0.0
                    type<Profile>() generates Profile("value") orDefaultAt 1.0
                    type<Profile>() generates Profile("value") orDefaultAt 0.percent
                    type<Profile>() generates Profile("value") orDefaultAt 100.percent
                    property<User, Profile>("profile") generates Profile("value") orDefaultAt 1.0
                    name<Profile>("profile") generates Profile("value") orDefaultAt 1.0
                    User::profile generates Profile("value") orDefaultAt 1.0
                }
                """.trimIndent(),
            )

        assertEquals(7, findings.size)
        assertEquals(
            """Use `type<Profile>() generates Profile("value")` instead of `type<Profile>() generates Profile("value") orDefaultAt 0.0`.""",
            findings[0].message,
        )
        assertEquals(
            """Use `type<Profile>() generates default` instead of `type<Profile>() generates Profile("value") orDefaultAt 1.0`.""",
            findings[1].message,
        )
        assertEquals(
            """Use `User::profile generates default` instead of `User::profile generates Profile("value") orDefaultAt 1.0`.""",
            findings[6].message,
        )
    }

    @Test
    fun `does not report non fixed probabilities or non generates rules`() {
        val findings =
            rule.lint(
                """
                fun configure(probability: Double) {
                    type<Profile>() generates Profile("value") orDefaultAt 0.3
                    type<Profile>() generates Profile("value") orDefaultAt 30.percent
                    type<Profile>() generates Profile("value") orDefaultAt probability
                    type<Profile>() generatesBy { Profile("value") } orDefaultAt 1.0
                    type<Profile>() generatesOneOf listOf(Profile("value")) orDefaultAt 1.0
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects fixed default probabilities`() {
        val code =
            """
            fun configure() {
                type<Profile>() generates Profile("value") orDefaultAt 0.0
                type<Profile>() generates Profile("value") orDefaultAt 1.0
                type<Profile>() generates Profile("value") orDefaultAt 0.percent
                type<Profile>() generates Profile("value") orDefaultAt 100.percent
                property<User, Profile>("profile") generates Profile("value") orDefaultAt 1.0
                name<Profile>("profile") generates Profile("value") orDefaultAt 1.0
                User::profile generates Profile("value") orDefaultAt 1.0
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferFixedDefaultProbability(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(7, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.generates

            import dev.s7a.fiktion.default

            fun configure() {
                type<Profile>() generates Profile("value")
                type<Profile>() generates default
                type<Profile>() generates Profile("value")
                type<Profile>() generates default
                property<User, Profile>("profile") generates default
                name<Profile>("profile") generates default
                User::profile generates default
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
