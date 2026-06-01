package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferTypeRuleForNonGenericTest {
    @Test
    fun `exact type rule is equivalent to non-generic type-family rule`() {
        val exactType =
            Fiktion {
                type<Boolean>() generatesBy { true }
            }
        val nonGenericTypeFamily =
            Fiktion {
                typeFamily<Boolean>() generatesBy { true }
            }

        assertEquals(nonGenericTypeFamily.fake<Boolean>(), exactType.fake<Boolean>())
    }
}
