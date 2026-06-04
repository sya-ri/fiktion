package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalFiktionApi::class)
class FiktionEnumMetadataTest {
    @Test
    fun `builder registers enum metadata by generated type`() {
        val metadata = statusMetadata()
        val builder = DefaultFiktionBuilder()

        builder.register(metadata)

        assertSame(metadata, builder.build().metadata[typeOf<Status>().nonNullTypeId()])
    }

    @Test
    fun `builder replaces enum metadata for the same generated type`() {
        val first = statusMetadata(entries = listOf(Status.ACTIVE))
        val second = statusMetadata(entries = listOf(Status.DELETED))
        val builder = DefaultFiktionBuilder()

        builder.register(first)
        builder.register(second)

        val metadata = builder.build().metadata[typeOf<Status>().nonNullTypeId()] as FiktionEnumMetadata<*>
        assertSame(second, metadata)
        assertEquals(listOf(Status.DELETED), metadata.entries)
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
