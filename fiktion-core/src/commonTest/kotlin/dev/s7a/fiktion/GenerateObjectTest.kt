package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalFiktionApi::class)
class GenerateObjectTest {
    @Test
    fun `fake constructs an object from registered metadata`() {
        val fiktion =
            Fiktion {
                register(userMetadata())
                type<String>() generates "generated-id"
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "generated-id"), user)
    }

    @Test
    fun `per-call property rules override generated object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadata())
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                User::id generates "user-1"
            }

        assertEquals(User(id = "user-1"), user)
    }

    @Test
    fun `generated object properties expose property context to matching rules`() {
        val fiktion =
            Fiktion {
                register(userMetadata())
                type<String>() generatesBy {
                    property?.name.orEmpty()
                }
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "id"), user)
    }

    @Test
    fun `fake recursively constructs nested objects from registered metadata`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithProfile())
                register(profileMetadata())
                type<String>() generatesBy {
                    path.segments.joinToString(".") { segment -> segment.name }
                }
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(
            User(
                id = "id",
                profile = Profile(nickname = "profile.nickname"),
            ),
            user,
        )
    }

    @Test
    fun `per-call nested path rules override generated nested object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithProfile())
                register(profileMetadata())
                type<String>() generates "generated"
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                (User::profile / Profile::nickname) generates "nick"
            }

        assertEquals(
            User(
                id = "generated",
                profile = Profile(nickname = "nick"),
            ),
            user,
        )
    }

    @Test
    fun `fake constructs nullable object properties from non-null metadata`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithOptionalProfile())
                register(profileMetadata())
                type<String>() generatesBy {
                    path.segments.joinToString(".") { segment -> segment.name }
                }
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(
            User(
                id = "id",
                optionalProfile = Profile(nickname = "optionalProfile.nickname"),
            ),
            user,
        )
    }

    /**
     * Returns metadata for constructing [User] from generated arguments.
     */
    private fun userMetadata(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                ),
        ) { values ->
            User(id = values[0] as String)
        }

    /**
     * Returns metadata for constructing [User] with a generated profile.
     */
    private fun userMetadataWithProfile(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "profile", type = typeOf<Profile>()),
                ),
        ) { values ->
            User(
                id = values[0] as String,
                profile = values[1] as Profile,
            )
        }

    /**
     * Returns metadata for constructing [User] with a generated optional profile.
     */
    private fun userMetadataWithOptionalProfile(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "optionalProfile", type = typeOf<Profile?>()),
                ),
        ) { values ->
            User(
                id = values[0] as String,
                optionalProfile = values[1] as Profile?,
            )
        }

    /**
     * Returns metadata for constructing [Profile] from generated arguments.
     */
    private fun profileMetadata(): FiktionObjectMetadata<Profile> =
        FiktionObjectMetadata(
            type = typeOf<Profile>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "nickname", type = typeOf<String>()),
                ),
        ) { values ->
            Profile(nickname = values[0] as String)
        }
}
