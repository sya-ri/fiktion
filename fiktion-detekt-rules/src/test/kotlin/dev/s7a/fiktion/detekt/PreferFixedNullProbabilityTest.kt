package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferFixedNullProbabilityTest {
    private val rule = PreferFixedNullProbability(Config.empty)

    @Test
    fun `reports fixed null probabilities`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String?>() generates "value" orNullAt 0.0
                    type<String?>() generates "value" orNullAt 1.0
                    type<String?>() generates "value" orNullAt 0.percent
                    type<String?>() generates "value" orNullAt 100.percent
                    property<User, Profile?>("profile") generates Profile("value") orNullAt 1.0
                    name<String?>("name") generates "value" orNullAt 1.0
                    User::nickname generates "value" orNullAt 0.0
                    User::nickname generates null orNullAt 1.0
                }
                """.trimIndent(),
            )

        assertEquals(8, findings.size)
        assertEquals(
            """Use `type<String?>() generates "value"` instead of `type<String?>() generates "value" orNullAt 0.0`.""",
            findings[0].message,
        )
        assertEquals(
            """Use `type<String?>() generates null` instead of `type<String?>() generates "value" orNullAt 1.0`.""",
            findings[1].message,
        )
        assertEquals(
            """Use `User::nickname generates null` instead of `User::nickname generates null orNullAt 1.0`.""",
            findings[7].message,
        )
    }

    @Test
    fun `does not report non fixed probabilities or unsafe null replacements`() {
        val findings =
            rule.lint(
                """
                fun configure(probability: Double) {
                    type<String?>() generates "value" orNullAt 0.3
                    type<String?>() generates "value" orNullAt 30.percent
                    type<String?>() generates "value" orNullAt probability
                    type<String>() generates "value" orNullAt 1.0
                    property(User::profile) generates Profile("value") orNullAt 1.0
                    User::profile generates Profile("value") orNullAt 1.0
                    type<String?>() generatesBy { "value" } orNullAt 1.0
                    type<String?>() generatesOneOf listOf("value") orNullAt 1.0
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects fixed null probabilities`() {
        val code =
            """
            fun configure() {
                type<String?>() generates "value" orNullAt 0.0
                type<String?>() generates "value" orNullAt 1.0
                type<String?>() generates "value" orNullAt 0.percent
                type<String?>() generates "value" orNullAt 100.percent
                property<User, Profile?>("profile") generates Profile("value") orNullAt 1.0
                name<String?>("name") generates "value" orNullAt 1.0
                User::nickname generates "value" orNullAt 0.0
                User::nickname generates null orNullAt 1.0
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferFixedNullProbability(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(8, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.generates

            fun configure() {
                type<String?>() generates "value"
                type<String?>() generates null
                type<String?>() generates "value"
                type<String?>() generates null
                property<User, Profile?>("profile") generates null
                name<String?>("name") generates null
                User::nickname generates "value"
                User::nickname generates null
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
