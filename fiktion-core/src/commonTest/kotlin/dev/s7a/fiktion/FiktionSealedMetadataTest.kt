package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalFiktionApi::class)
class FiktionSealedMetadataTest {
    @Test
    fun `builder registers sealed metadata by generated type`() {
        val metadata = messageMetadata()
        val builder = DefaultFiktionBuilder()

        builder.register(metadata)

        assertSame(metadata, builder.build().metadata[typeOf<Message>().nonNullTypeId()])
    }

    @Test
    fun `builder replaces sealed metadata for the same generated type`() {
        val first = messageMetadata(subtypes = listOf(typeOf<TextMessage>()))
        val second = messageMetadata(subtypes = listOf(typeOf<ImageMessage>()))
        val builder = DefaultFiktionBuilder()

        builder.register(first)
        builder.register(second)

        val metadata = builder.build().metadata[typeOf<Message>().nonNullTypeId()] as FiktionSealedMetadata<*>
        assertSame(second, metadata)
        assertEquals(listOf(typeOf<ImageMessage>()), metadata.subtypes)
    }

    /**
     * Returns test metadata for [Message].
     */
    private fun messageMetadata(
        subtypes: List<kotlin.reflect.KType> = listOf(typeOf<TextMessage>(), typeOf<ImageMessage>()),
    ): FiktionSealedMetadata<Message> =
        FiktionSealedMetadata(
            type = typeOf<Message>(),
            subtypes = subtypes,
        )
}
