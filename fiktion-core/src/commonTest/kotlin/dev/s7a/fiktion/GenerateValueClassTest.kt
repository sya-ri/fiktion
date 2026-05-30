package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    fun `per-call property rules override generated value class underlying properties`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "generated"
            }

        val userId =
            fiktion.fake<UserId>(seed = 123) {
                UserId::value generates "configured"
            }

        assertEquals(UserId("configured"), userId)
    }

    @Test
    fun `property rules override generated nullable value class underlying properties`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "generated"
                UserId::value generates "configured"
            }

        val userId = fiktion.fake<UserId?>(seed = 123)

        assertEquals(UserId("configured"), userId)
    }

    @Test
    fun `name rules override generated value class underlying properties`() {
        val fiktion =
            Fiktion {
                register(userIdMetadata())
                type<String>() generates "generated"
            }

        val userId =
            fiktion.fake<UserId>(seed = 123) {
                name<String>("value") generates "configured"
            }

        assertEquals(UserId("configured"), userId)
    }

    @Test
    fun `property rules override nested generated value class underlying properties`() {
        val fiktion =
            Fiktion {
                register(userAccountMetadata())
                register(userIdMetadata())
                type<String>() generates "generated"
            }

        val account =
            fiktion.fake<UserAccount>(seed = 123) {
                (UserAccount::id / UserId::value) generates "configured"
            }

        assertEquals(UserAccount(id = UserId("configured")), account)
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

    @Test
    fun `fake reports underlying value type when value metadata cannot be generated`() {
        val fiktion =
            Fiktion {
                register(complexUserIdMetadata())
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<ComplexUserId>(seed = 1)
            }

        assertTrue(error.message.orEmpty().contains("Generated value metadata was found"))
        assertTrue(error.message.orEmpty().contains("ComplexUserId"))
        assertTrue(error.message.orEmpty().contains("underlying value type"))
        assertTrue(error.message.orEmpty().contains("ComplexUserIdValue"))
        assertTrue(error.message.orEmpty().contains("type<"))
        assertTrue(error.message.orEmpty().contains("generatesBy { ... }"))
        assertTrue(error.cause is CannotGenerateException)
    }

    /**
     * Returns test metadata for [UserId].
     */
    private fun userIdMetadata(): FiktionValueMetadata<UserId> =
        FiktionValueMetadata(
            type = typeOf<UserId>(),
            underlyingType = typeOf<String>(),
            propertyName = "value",
        ) { value ->
            UserId(value as String)
        }

    /**
     * Returns test metadata for [UserAccount].
     */
    private fun userAccountMetadata(): FiktionObjectMetadata<UserAccount> =
        FiktionObjectMetadata(
            type = typeOf<UserAccount>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<UserId>()),
                ),
        ) { values ->
            UserAccount(id = values[0].valueOrDefault(defaultValue = null) as UserId)
        }

    /**
     * Returns test metadata for [ComplexUserId].
     */
    private fun complexUserIdMetadata(): FiktionValueMetadata<ComplexUserId> =
        FiktionValueMetadata(
            type = typeOf<ComplexUserId>(),
            underlyingType = typeOf<ComplexUserIdValue>(),
            propertyName = "value",
        ) { value ->
            ComplexUserId(value as ComplexUserIdValue)
        }
}

private data class UserAccount(
    val id: UserId,
)
