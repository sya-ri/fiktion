package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.CannotGenerateException
import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalFiktionApi::class)
class GenerateValueClassTest {
    @Test
    fun `fake constructs a value class from registered value metadata`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "user-1"
            }

        val userId = fiktion.fake<UserId>(seed = 123)

        assertEquals(UserId("user-1"), userId)
    }

    @Test
    fun `fake constructs a nullable value class from non-null metadata`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "user-1"
            }

        val userId = fiktion.fake<UserId?>(seed = 123)

        assertEquals(UserId("user-1"), userId)
    }

    @Test
    fun `explicit type rules override registered value metadata`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "underlying"
                type<UserId>() generates UserId("explicit")
            }

        val userId = fiktion.fake<UserId>(seed = 123)

        assertEquals(UserId("explicit"), userId)
    }

    @Test
    fun `isolated instance value metadata does not leak to other isolated instances`() {
        val registered =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "user-1"
            }
        val empty = Fiktion()

        assertEquals(UserId("user-1"), registered.fake<UserId>())
        assertFailsWith<CannotGenerateException> {
            empty.fake<UserId>(seed = 1)
        }
    }

    /**
     * Returns test metadata for [UserId].
     */
    private fun userIdMetadata(): FiktionValueMetadata<UserId> =
        FiktionValueMetadata(
            type = typeOf<UserId>(),
            underlyingType = typeOf<String>(),
        ) { value ->
            UserId(value as String)
        }
}
