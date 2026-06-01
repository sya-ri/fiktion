package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidEmptyOneOfTest {
    @Test
    fun `reports empty oneOf value sets`() {
        val findings =
            AvoidEmptyOneOf(Config.empty).lint(
                """
                fun configure() {
                    type<Int>() generatesBy { oneOf() }
                    type<Int>() generatesBy { oneOf(emptyList()) }
                    type<Int>() generatesOneOf emptyList()
                    type<Int>() generatesOneOf listOf(1)
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
    }
}
