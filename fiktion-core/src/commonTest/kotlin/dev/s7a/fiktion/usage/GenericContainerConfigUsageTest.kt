package dev.s7a.fiktion.usage

import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.UniqueElementStrategy
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericContainerConfigUsageTest {
    @Test
    fun `generic list can use collection size config without importing using`() {
        assertEquals(5, listOfFake<Int>().size)
    }

    @Test
    fun `generic set can use collection size config without importing using`() {
        assertEquals(5, setOfFake<Int>().size)
    }

    @Test
    fun `generic map can use map size config without importing using`() {
        assertEquals(5, mapOfFake<String, Int>().size)
    }
}

private inline fun <reified T> listOfFake(): List<T> =
    fake(seed = 1) {
        this using FiktionConfig.Collection.size(5)
    }

private inline fun <reified T> setOfFake(): Set<T> =
    fake(seed = 1) {
        this using FiktionConfig.Collection.size(5)
        this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 16))
    }

private inline fun <reified K, reified V> mapOfFake(): Map<K, V> =
    fake(seed = 1) {
        this using FiktionConfig.Map.size(5)
    }
