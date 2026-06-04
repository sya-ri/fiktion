package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    fun `fake reports constructor argument when generated object property cannot be generated`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithProfile())
                type<String>() generates "id"
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<User>(seed = 123)
            }

        assertEquals(
            """
            Cannot generate ${typeOf<User>()}.

            Failed while generating constructor argument profile: ${typeOf<Profile>()}.

            Add a rule for the nested type or property:
            type<${typeOf<Profile>()}>() generatesBy { ... }
            property<${typeOf<User>()}, ${typeOf<Profile>()}>("profile") generatesBy { ... }
            """.trimIndent(),
            error.message,
        )
        val causeMessage = error.cause?.message.orEmpty()
        assertTrue(causeMessage.contains("Generation request:"))
        assertTrue(causeMessage.contains("- property: profile"))
        assertTrue(causeMessage.contains("Current Fiktion configuration:"))
        assertTrue(error.cause is CannotGenerateException)
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
    fun `per-call nested property blocks configure generated nested object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithProfile())
                register(profileMetadata())
                type<String>() generates "generated"
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                User::profile {
                    Profile::nickname generates "nick"
                }
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
    fun `generates auto uses automatic generation for a property`() {
        val fiktion =
            Fiktion {
                register(userMetadata())
                type<String>() generates "configured"
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                User::id generates auto
            }

        assertTrue(user.id.isNotBlank())
        assertTrue(user.id != "configured")
        assertEquals(
            user,
            fiktion.fake<User>(seed = 123) {
                User::id generates auto
            },
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

    @Test
    fun `property reference rules generate null for nullable object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithOptionalProfile())
                type<String>() generates "id"
                User::optionalProfile generates null
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "id", optionalProfile = null), user)
    }

    @Test
    fun `property reference rules generate non-null values for nullable object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithOptionalProfile())
                type<String>() generates "id"
                User::optionalProfile generates Profile(nickname = "generated")
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "id", optionalProfile = Profile(nickname = "generated")), user)
    }

    @Test
    fun `per-call property reference rules generate null for nullable object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithOptionalProfile())
                register(profileMetadata())
                type<String>() generates "id"
                type<Profile>() generates Profile(nickname = "generated")
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                User::optionalProfile generates null
            }

        assertEquals(User(id = "id", optionalProfile = null), user)
    }

    @Test
    fun `per-call property reference rules generate non-null values for nullable object properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithOptionalProfile())
                register(profileMetadata())
                type<String>() generates "id"
                type<Profile>() generates Profile(nickname = "fallback")
            }
        val notNullProfile = Profile(nickname = "per-call")

        val user =
            fiktion.fake<User>(seed = 123) {
                User::optionalProfile generates notNullProfile
            }

        assertEquals(User(id = "id", optionalProfile = notNullProfile), user)
    }

    @Test
    fun `fake uses default values with configured probabilities`() {
        assertDefaultProfileCount(defaultProbability = 0.0, expectedRange = 0..0)
        assertDefaultProfileCount(defaultProbability = 0.3, expectedRange = 250..350)
        assertDefaultProfileCount(defaultProbability = 0.5, expectedRange = 400..600)
        assertDefaultProfileCount(defaultProbability = 1.0, expectedRange = 1_000..1_000)
    }

    @Test
    fun `fake generates defaultable properties when default probability is omitted`() {
        assertDefaultProfileCount(defaultProbability = null, expectedRange = 400..600)
    }

    @Test
    fun `fake generates defaultable built-in properties when default probability is omitted`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultId())
            }
        val defaultCount =
            (0 until 1_000).count { seed ->
                fiktion.fake<User>(seed = seed.toLong()).id == "default-id"
            }

        assertTrue(defaultCount in 400..600, "Expected about 50% defaults, but got $defaultCount.")
    }

    @Test
    fun `fake generates nullable defaultable properties with non-null defaults from automatic values nulls or defaults`() {
        val defaultProfile = Profile(nickname = "default")
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultProfile))
                register(profileMetadata())
                type<String>() generates "id"
            }
        val generatedProfile = Profile(nickname = "id")
        val users = (0 until 1_000).map { seed -> fiktion.fake<User>(seed = seed.toLong()) }
        val defaultCount = users.count { user -> user.optionalProfile == defaultProfile }
        val nullCount = users.count { user -> user.optionalProfile == null }
        val generatedCount = users.count { user -> user.optionalProfile == generatedProfile }

        assertTrue(defaultCount in 400..600, "Expected about 50% defaults, but got $defaultCount.")
        assertTrue(nullCount in 150..350, "Expected about 25% nulls, but got $nullCount.")
        assertTrue(generatedCount in 150..350, "Expected about 25% generated values, but got $generatedCount.")
    }

    @Test
    fun `fake generates nullable defaultable properties with null defaults from automatic values or nulls`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultValue = null))
                register(profileMetadata())
                type<String>() generates "id"
            }
        val generatedProfile = Profile(nickname = "id")
        val users = (0 until 1_000).map { seed -> fiktion.fake<User>(seed = seed.toLong()) }
        val nullCount = users.count { user -> user.optionalProfile == null }
        val generatedCount = users.count { user -> user.optionalProfile == generatedProfile }

        assertTrue(nullCount in 650..850, "Expected about 75% nulls, but got $nullCount.")
        assertTrue(generatedCount in 150..350, "Expected about 25% generated values, but got $generatedCount.")
    }

    @Test
    fun `explicit generated values do not use nullable defaults or nulls implicitly`() {
        val generatedProfile = Profile(nickname = "generated")
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultValue = Profile(nickname = "default")))
                type<String>() generates "id"
                User::optionalProfile generates generatedProfile
            }

        val users = (0 until 1_000).map { seed -> fiktion.fake<User>(seed = seed.toLong()) }

        assertTrue(users.all { user -> user.optionalProfile == generatedProfile })
    }

    @Test
    fun `property reference rules generate constructor defaults for defaultable properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultProfile())
                type<String>() generates "id"
                User::profile generates default
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "id", profile = Profile(nickname = "default")), user)
    }

    @Test
    fun `type rules generate constructor defaults for defaultable properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultProfile())
                type<String>() generates "id"
                type<Profile>() generates default
            }

        val user = fiktion.fake<User>(seed = 123)

        assertEquals(User(id = "id", profile = Profile(nickname = "default")), user)
    }

    @Test
    fun `per-call property reference rules generate constructor defaults for defaultable properties`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultProfile())
                type<String>() generates "id"
                type<Profile>() generates Profile(nickname = "generated")
            }

        val user =
            fiktion.fake<User>(seed = 123) {
                User::profile generates default
            }

        assertEquals(User(id = "id", profile = Profile(nickname = "default")), user)
    }

    @Test
    fun `fake reports constructor argument when default is requested for a non-defaultable property`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithProfile())
                type<String>() generates "id"
                User::profile generates default
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<User>(seed = 123)
            }

        assertTrue(error.message.orEmpty().contains("Rule requested the constructor default for profile"))
        assertTrue(error.message.orEmpty().contains("has no default value"))
        assertTrue(error.message.orEmpty().contains("generates default"))
    }

    @Test
    fun `fake uses nullable default values with configured probabilities`() {
        assertOptionalDefaultProfileCount(defaultProbability = 0.0, expectedRange = 0..0)
        assertOptionalDefaultProfileCount(defaultProbability = 0.3, expectedRange = 250..350)
        assertOptionalDefaultProfileCount(defaultProbability = 0.5, expectedRange = 400..600)
        assertOptionalDefaultProfileCount(defaultProbability = 1.0, expectedRange = 1_000..1_000)
    }

    @Test
    fun `fake uses nullable null defaults with configured probabilities`() {
        assertNullDefaultProfileCount(defaultProbability = 0.0, expectedRange = 0..0)
        assertNullDefaultProfileCount(defaultProbability = 0.3, expectedRange = 250..350)
        assertNullDefaultProfileCount(defaultProbability = 0.5, expectedRange = 400..600)
        assertNullDefaultProfileCount(defaultProbability = 1.0, expectedRange = 1_000..1_000)
    }

    @Test
    fun `fake replaces a nullable default value with null`() {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultValue = Profile(nickname = "default")))
                type<String>() generates "id"
                val spec = property<User, Profile?>("optionalProfile") generates Profile(nickname = "generated")
                spec orDefaultAt 0.0
                spec orNullAt 1.0
            }

        assertEquals(User(id = "id", optionalProfile = null), fiktion.fake<User>(seed = 123))
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
            User(id = values[0].valueOrDefault(defaultValue = null) as String)
        }

    /**
     * Returns metadata for constructing [User] with a defaultable id.
     */
    private fun userMetadataWithDefaultId(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>(), hasDefault = true),
                ),
        ) { values ->
            User(id = values[0].valueOrDefault(defaultValue = "default-id") as String)
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
                id = values[0].valueOrDefault(defaultValue = null) as String,
                profile = values[1].valueOrDefault(defaultValue = null) as Profile,
            )
        }

    /**
     * Returns metadata for constructing [User] with a defaultable profile.
     */
    private fun userMetadataWithDefaultProfile(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "profile", type = typeOf<Profile>(), hasDefault = true),
                ),
        ) { values ->
            User(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                profile = values[1].valueOrDefault(defaultValue = Profile(nickname = "default")) as Profile,
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
                id = values[0].valueOrDefault(defaultValue = null) as String,
                optionalProfile = values[1].valueOrDefault(defaultValue = null) as Profile?,
            )
        }

    /**
     * Returns metadata for constructing [User] with a defaultable optional profile.
     */
    private fun userMetadataWithDefaultOptionalProfile(defaultValue: Profile?): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "optionalProfile", type = typeOf<Profile?>(), hasDefault = true),
                ),
        ) { values ->
            User(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                optionalProfile = values[1].valueOrDefault(defaultValue = defaultValue) as Profile?,
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
            Profile(nickname = values[0].valueOrDefault(defaultValue = null) as String)
        }

    /**
     * Asserts how often the non-null profile default is used across deterministic sample seeds.
     */
    private fun assertDefaultProfileCount(
        defaultProbability: Double?,
        expectedRange: IntRange,
    ) {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultProfile())
                register(profileMetadata())
                type<String>() generates "id"
                if (defaultProbability != null) {
                    property<User, Profile>("profile") generates Profile(nickname = "generated") orDefaultAt defaultProbability
                }
            }

        val defaultCount =
            (0 until 1_000).count { seed ->
                fiktion.fake<User>(seed = seed.toLong()).profile == Profile(nickname = "default")
            }

        assertTrue(defaultCount in expectedRange, "Expected $expectedRange defaults, but got $defaultCount.")
    }

    /**
     * Asserts how often the nullable profile default is used across deterministic sample seeds.
     */
    private fun assertOptionalDefaultProfileCount(
        defaultProbability: Double,
        expectedRange: IntRange,
    ) {
        val defaultProfile = Profile(nickname = "default")
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultProfile))
                type<String>() generates "id"
                val spec = property<User, Profile?>("optionalProfile") generates Profile(nickname = "generated")
                spec orNullAt 0.0
                spec orDefaultAt defaultProbability
            }

        val defaultCount =
            (0 until 1_000).count { seed ->
                fiktion.fake<User>(seed = seed.toLong()).optionalProfile == defaultProfile
            }

        assertTrue(defaultCount in expectedRange, "Expected $expectedRange nullable defaults, but got $defaultCount.")
    }

    /**
     * Asserts how often the nullable null default is used across deterministic sample seeds.
     */
    private fun assertNullDefaultProfileCount(
        defaultProbability: Double,
        expectedRange: IntRange,
    ) {
        val fiktion =
            Fiktion {
                register(userMetadataWithDefaultOptionalProfile(defaultValue = null))
                type<String>() generates "id"
                val spec = property<User, Profile?>("optionalProfile") generates Profile(nickname = "generated")
                spec orNullAt 0.0
                spec orDefaultAt defaultProbability
            }

        val nullCount =
            (0 until 1_000).count { seed ->
                fiktion.fake<User>(seed = seed.toLong()).optionalProfile == null
            }

        assertTrue(nullCount in expectedRange, "Expected $expectedRange null defaults, but got $nullCount.")
    }
}
