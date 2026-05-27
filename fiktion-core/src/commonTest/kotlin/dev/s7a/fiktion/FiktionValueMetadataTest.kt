package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalFiktionApi::class)
class FiktionValueMetadataTest {
    @Test
    fun `builder registers value metadata by generated type`() {
        val metadata = userIdMetadata()
        val builder = DefaultFiktionBuilder()

        builder.register(metadata)

        assertSame(metadata, builder.build().metadata[typeOf<UserId>().nonNullTypeId()])
    }

    @Test
    fun `builder replaces value metadata for the same generated type`() {
        val first = userIdMetadata(prefix = "first")
        val second = userIdMetadata(prefix = "second")
        val builder = DefaultFiktionBuilder()

        builder.register(first)
        builder.register(second)

        val metadata = builder.build().metadata[typeOf<UserId>().nonNullTypeId()]
        assertSame(second, metadata)
        assertEquals(UserId("second-value"), second.construct("value"))
    }

    @Test
    fun `builder replaces metadata for the same generated type across metadata kinds`() {
        val valueMetadata = userIdMetadata(prefix = "value")
        val objectMetadata = userIdObjectMetadata(prefix = "object")
        val builder = DefaultFiktionBuilder()

        builder.register(valueMetadata)
        builder.register(objectMetadata)

        val metadata = builder.build().metadata[typeOf<UserId>().nonNullTypeId()]
        assertSame(objectMetadata, metadata)
    }

    @Test
    fun `overlay value metadata replaces base metadata for the same generated type`() {
        val base =
            FiktionConfig(
                metadata =
                    mapOf(
                        typeOf<UserId>().nonNullTypeId() to userIdMetadata(prefix = "base"),
                    ),
            )
        val overlay =
            FiktionConfig(
                metadata =
                    mapOf(
                        typeOf<UserId>().nonNullTypeId() to userIdMetadata(prefix = "overlay"),
                    ),
            )

        val config =
            base.overlaidBy(
                other = overlay,
                rulePrecedence = RulePrecedence.INSTANCE,
            )

        val metadata = config.metadata[typeOf<UserId>().nonNullTypeId()] as FiktionValueMetadata<*>
        assertEquals(UserId("overlay-value"), metadata.construct("value"))
    }

    /**
     * Returns test metadata for [UserId].
     */
    private fun userIdMetadata(prefix: String? = null): FiktionValueMetadata<UserId> =
        FiktionValueMetadata(
            type = typeOf<UserId>(),
            underlyingType = typeOf<String>(),
        ) { value ->
            UserId(listOfNotNull(prefix, value as String).joinToString("-"))
        }

    /**
     * Returns object metadata for [UserId] replacement tests.
     */
    private fun userIdObjectMetadata(prefix: String): FiktionObjectMetadata<UserId> =
        FiktionObjectMetadata(
            type = typeOf<UserId>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "value", type = typeOf<String>()),
                ),
        ) { values ->
            UserId("$prefix-${values[0].valueOrDefault(defaultValue = null) as String}")
        }
}
