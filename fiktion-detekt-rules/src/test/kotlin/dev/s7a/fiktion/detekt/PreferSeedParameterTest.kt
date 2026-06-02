package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferSeedParameterTest {
    private val rule = PreferSeedParameter(Config.empty)

    @Test
    fun `reports withSeed at the start of fake blocks`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User> {
                        withSeed(123)
                        User::id generates "id"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Pass this seed as `fake(seed = 123)` instead of declaring `withSeed` inside the fake block.",
            findings[0].message,
        )
    }

    @Test
    fun `reports single withSeed after other fake block declarations`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User> {
                        User::id generates "id"
                        this withSeed 123
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Pass this seed as `fake(seed = 123)` instead of declaring `withSeed` inside the fake block.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report fake calls that already pass seed`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User>(seed = 123) {
                        User::id generates "id"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report multiple withSeed declarations`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User> {
                        withSeed(123)
                        withSeed(456)
                        User::id generates "id"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }
}
