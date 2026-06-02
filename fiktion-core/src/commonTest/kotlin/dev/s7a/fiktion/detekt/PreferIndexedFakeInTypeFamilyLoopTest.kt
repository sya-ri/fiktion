package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferIndexedFakeInTypeFamilyLoopTest {
    @Test
    fun `indexed type-family fake calls vary FakeContext index while keeping argument index`() {
        val repeatedIndex =
            Fiktion {
                type<Int>() generatesBy { index }
                typeFamily<IndexedBox<*>>() generatesBy {
                    IndexedBox(List(3) { fake(0) })
                }
            }
        val indexed =
            Fiktion {
                type<Int>() generatesBy { index }
                typeFamily<IndexedBox<*>>() generatesBy {
                    IndexedBox(List(3) { index -> fake(index, argumentIndex = 0) })
                }
            }

        assertEquals(IndexedBox(listOf(0, 0, 0)), repeatedIndex.fake<IndexedBox<Int>>())
        assertEquals(IndexedBox(listOf(0, 1, 2)), indexed.fake<IndexedBox<Int>>())
    }
}

private data class IndexedBox<T>(
    val values: List<T>,
)
