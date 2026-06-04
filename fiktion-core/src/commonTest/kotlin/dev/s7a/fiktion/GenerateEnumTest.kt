package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateEnumTest {
    @Test
    fun `fake selects an entry from registered enum metadata`() {
        val fiktion =
            Fiktion {
                register(statusMetadata())
            }

        val status = fiktion.fake<Status>(seed = 123)

        assertTrue(status in Status.entries)
    }

    @Test
    fun `fake changes enum entries with different seeds`() {
        val fiktion =
            Fiktion {
                register(statusMetadata())
            }

        val statuses = (0L until 20L).map { seed -> fiktion.fake<Status>(seed = seed) }.toSet()

        assertTrue(statuses.size > 1, "Expected different seeds to select multiple enum entries.")
    }

    @Test
    fun `explicit type rules override registered enum metadata`() {
        val fiktion =
            Fiktion {
                register(statusMetadata())
                type<Status>() generates Status.DELETED
            }

        val status = fiktion.fake<Status>(seed = 123)

        assertEquals(Status.DELETED, status)
    }

    @Test
    fun `isolated instance enum metadata does not leak to other isolated instances`() {
        val registered =
            Fiktion {
                register(statusMetadata(entries = listOf(Status.ACTIVE)))
            }
        val empty = Fiktion()

        assertEquals(Status.ACTIVE, registered.fake<Status>())
        assertFailsWith<CannotGenerateException> {
            empty.fake<Status>(seed = 1)
        }
    }

    @Test
    fun `fake fails when registered enum metadata has no entries`() {
        val fiktion =
            Fiktion {
                register(statusMetadata(entries = emptyList()))
            }

        assertFailsWith<CannotGenerateException> {
            fiktion.fake<Status>(seed = 123)
        }
    }

    /**
     * Returns test metadata for [Status].
     */
    private fun statusMetadata(entries: List<Status> = Status.entries): FiktionEnumMetadata<Status> =
        FiktionEnumMetadata(
            type = typeOf<Status>(),
            entries = entries,
        )
}
