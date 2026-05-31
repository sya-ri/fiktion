package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.element
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.key
import dev.s7a.fiktion.value
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferContainerPartFakeHelpersTest {
    @Test
    fun `container part helpers apply element key and value target configuration`() {
        val raw =
            Fiktion {
                type<String>() generates ""
                type<Int>() generates 0
                typeFamily<List<*>>() generatesBy {
                    List(2) { index -> fake(index, argumentIndex = 0) }
                }
                typeFamily<Map<*, *>>() generatesBy {
                    List(2) { index -> fake(index, argumentIndex = 0) to fake(index, argumentIndex = 1) }.toMap()
                }
            }
        val helper =
            Fiktion {
                type<String>() generates ""
                type<Int>() generates 0
                typeFamily<List<*>>() generatesBy {
                    List(2) { index -> fakeElement(index) }
                }
                typeFamily<Map<*, *>>() generatesBy {
                    List(2) { index -> fakeKey(index) to fakeValue(index) }.toMap()
                }
            }

        assertEquals(
            listOf("", ""),
            raw.fake<List<String>> {
                element generatesBy { "element-$index" }
            },
        )
        assertEquals(
            mapOf("" to 0),
            raw.fake<Map<String, Int>> {
                key generatesBy { "key-$index" }
                value generates 100
            },
        )
        assertEquals(
            listOf("element-0", "element-1"),
            helper.fake<List<String>> {
                element generatesBy { "element-$index" }
            },
        )
        assertEquals(
            mapOf("key-0" to 100, "key-1" to 100),
            helper.fake<Map<String, Int>> {
                key generatesBy { "key-$index" }
                value generates 100
            },
        )
    }
}
