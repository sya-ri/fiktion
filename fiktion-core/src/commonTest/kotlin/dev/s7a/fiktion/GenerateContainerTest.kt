package dev.s7a.fiktion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateContainerTest {
    @Test
    fun `fake generates nested lists without configuration`() {
        val value = fake<List<List<Int>>>(seed = 1)

        assertTrue(value.size in FiktionConfig.Collection.size.defaultValue)
        assertTrue(value.all { element -> element.size in FiktionConfig.Collection.size.defaultValue })
    }

    @Test
    fun `fake generates nested maps without configuration`() {
        val value = fake<Map<String, Map<String, Int>>>(seed = 1)

        assertTrue(value.size in FiktionConfig.Map.size.defaultValue)
        assertTrue(value.values.all { element -> element.size in FiktionConfig.Map.size.defaultValue })
    }

    @Test
    fun `fake generates mixed nested containers without configuration`() {
        val value = fake<List<Map<String, Set<Int>>>>(seed = 1)

        assertTrue(value.size in FiktionConfig.Collection.size.defaultValue)
        assertTrue(value.all { element -> element.size in FiktionConfig.Map.size.defaultValue })
        assertTrue(
            value
                .flatMap { element ->
                    element.values
                }.all { element -> element.size in FiktionConfig.Collection.size.defaultValue },
        )
    }

    @Test
    fun `fake generates nested sequences deterministically`() {
        val first = fake<Sequence<List<Int>>>(seed = 1).map { element -> element.toList() }.toList()
        val second = fake<Sequence<List<Int>>>(seed = 1).map { element -> element.toList() }.toList()

        assertEquals(first, second)
        assertTrue(first.size in FiktionConfig.Collection.size.defaultValue)
        assertTrue(first.all { element -> element.size in FiktionConfig.Collection.size.defaultValue })
    }
}
