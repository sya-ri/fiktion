package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalFiktionApi::class)
class FiktionObjectMetadataTest {
    @Test
    fun `builder registers object metadata by generated type`() {
        val metadata = userMetadata("registered")
        val builder = DefaultFiktionBuilder()

        builder.register(metadata)

        assertSame(metadata, builder.build().metadata[typeOf<User>().nonNullTypeId()])
    }

    @Test
    fun `builder replaces object metadata for the same generated type`() {
        val first = userMetadata("first")
        val second = userMetadata("second")
        val builder = DefaultFiktionBuilder()

        builder.register(first)
        builder.register(second)

        val metadata = builder.build().metadata[typeOf<User>().nonNullTypeId()]
        assertSame(second, metadata)
        assertEquals(User(id = "second"), second.construct(emptyList()))
    }

    @Test
    fun `overlay metadata replaces base metadata for the same generated type`() {
        val base =
            FiktionConfig(
                metadata =
                    mapOf(
                        typeOf<User>().nonNullTypeId() to userMetadata("base"),
                        typeOf<Profile>().nonNullTypeId() to profileMetadata("profile"),
                    ),
            )
        val overlay =
            FiktionConfig(
                metadata =
                    mapOf(
                        typeOf<User>().nonNullTypeId() to userMetadata("overlay"),
                    ),
            )

        val config =
            base.overlaidBy(
                other = overlay,
                rulePrecedence = RulePrecedence.INSTANCE,
            )

        val userMetadata = config.metadata[typeOf<User>().nonNullTypeId()] as FiktionObjectMetadata<*>
        val profileMetadata = config.metadata[typeOf<Profile>().nonNullTypeId()] as FiktionObjectMetadata<*>
        assertEquals(User(id = "overlay"), userMetadata.construct(emptyList()))
        assertEquals(Profile(nickname = "profile"), profileMetadata.construct(emptyList()))
    }

    /**
     * Returns test metadata for [User].
     */
    private fun userMetadata(id: String): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                ),
        ) {
            User(id = id)
        }

    /**
     * Returns test metadata for [Profile].
     */
    private fun profileMetadata(nickname: String): FiktionObjectMetadata<Profile> =
        FiktionObjectMetadata(
            type = typeOf<Profile>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "nickname", type = typeOf<String>()),
                ),
        ) {
            Profile(nickname = nickname)
        }
}
