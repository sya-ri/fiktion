package dev.s7a.fiktion.compiler

import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.ExperimentalFiktionApi
import dev.s7a.fiktion.FakeType
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.Probability
import dev.s7a.fiktion.auto
import dev.s7a.fiktion.compiler.fixture.ExternalFactoryConstructorUser
import dev.s7a.fiktion.compiler.fixture.ExternalFactoryRole
import dev.s7a.fiktion.compiler.fixture.resetExternalFactoryConstructorRegistrarGuard
import dev.s7a.fiktion.constructsBy
import dev.s7a.fiktion.default
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import java.util.concurrent.atomic.AtomicReference
import kotlin.jvm.JvmInline
import kotlin.reflect.KFunction2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GeneratedMetadataSmokeTest {
    @Test
    fun `compiler plugin registers generated metadata before fake calls`() {
        assertEquals(fake<GeneratedUser>(seed = 123), fake<GeneratedUser>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for regular classes`() {
        val user = fake<GeneratedRegularUser>(seed = 123)

        assertEquals(fake<GeneratedRegularUser>(seed = 123).id, user.id)
    }

    @Test
    fun `compiler plugin registers generated metadata for private constructor properties`() {
        val user =
            Fiktion {
                property<GeneratedRegularUserWithPrivateProperty, String>("id") generates "configured-id"
            }.fake<GeneratedRegularUserWithPrivateProperty>(seed = 123)

        assertEquals("configured-id", user.id())
    }

    @Test
    fun `compiler plugin registers generated metadata for constructor parameters`() {
        val user =
            Fiktion {
                property<GeneratedRegularUserWithConstructorParameter, String>("id") generates "configured-id"
            }.fake<GeneratedRegularUserWithConstructorParameter>(seed = 123)

        assertEquals("configured-id", user.id)
    }

    @Test
    fun `compiler plugin registers generated metadata for constructor property type arguments`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Collection.size(1)
            }
        val user = fiktion.fake<GeneratedRegularUserWithProfiles>(seed = 123)

        assertEquals(user, fiktion.fake<GeneratedRegularUserWithProfiles>(seed = 123))
        assertTrue(user.profiles.isNotEmpty())
        assertTrue(
            user.profiles
                .first()
                .bio
                .isNotBlank(),
        )
    }

    @Test
    fun `compiler plugin substitutes concrete type arguments into generic constructor properties`() {
        val value = fake<GeneratedHolder<GeneratedBoxImpl<String>>>(seed = 123)

        assertTrue(value.box.value.isNotBlank())
    }

    @Test
    fun `compiler plugin uses substituted generic constructor property types for property rules`() {
        val value =
            fake<GeneratedHolder<GeneratedBoxImpl<String>>>(seed = 123) {
                property(GeneratedHolder<GeneratedBoxImpl<String>>::box) generates GeneratedBoxImpl("value")
            }

        assertEquals(GeneratedBoxImpl("value"), value.box)
    }

    @Test
    fun `compiler plugin registers generated metadata for dependency data classes`() {
        val type = fake<FakeType>(seed = 123)

        assertEquals(type, fake<FakeType>(seed = 123))
        assertTrue(type.id.isNotBlank())
    }

    @Test
    fun `compiler plugin generated metadata supports dependent property rules`() {
        val user =
            fake<GeneratedDependentUser>(seed = 123) {
                GeneratedDependentUser::id generates "user-1"
                GeneratedDependentUser::email.dependsOn(GeneratedDependentUser::id) generatesBy { id ->
                    "$id@example.test"
                }
            }

        assertEquals(GeneratedDependentUser(id = "user-1", email = "user-1@example.test"), user)
    }

    @Test
    fun `compiler plugin generated metadata supports dependent property rules with different dependency types`() {
        val label =
            fake<GeneratedDependentUserLabel>(seed = 123) {
                GeneratedDependentUserLabel::id generates "user-1"
                GeneratedDependentUserLabel::score generates 42
                GeneratedDependentUserLabel::label
                    .dependsOn(
                        GeneratedDependentUserLabel::id,
                        GeneratedDependentUserLabel::score,
                    ).generatesBy { id, score ->
                        "$id:$score"
                    }
            }

        assertEquals(
            GeneratedDependentUserLabel(
                id = "user-1",
                score = 42,
                label = "user-1:42",
            ),
            label,
        )
    }

    @Test
    fun `compiler plugin registers generated metadata for dependency value classes`() {
        val fiktion =
            Fiktion {
                type<Double>() generates 0.5
            }

        assertEquals(Probability(0.5), fiktion.fake<Probability>(seed = 123))
    }

    @Test
    fun `compiler plugin uses primary constructors for regular classes`() {
        val user = fake<GeneratedRegularUserWithSecondaryConstructor>(seed = 123)

        assertEquals(fake<GeneratedRegularUserWithSecondaryConstructor>(seed = 123).id, user.id)
        assertEquals("primary", user.source)
    }

    @Test
    fun `compiler plugin skips regular classes without primary constructors`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedRegularUserWithoutPrimaryConstructor>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips vararg primary constructors`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedVarargConstructorUser>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips abstract classes`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedAbstractUser>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips interfaces`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedUserContract>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips fun interfaces`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedUserFactory>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips annotation classes`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedUserAnnotation>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin skips inner classes`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedOuterUser.GeneratedInnerUser>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin registers generated metadata for local classes`() {
        class GeneratedLocalUser(
            /**
             * Local user identifier.
             */
            val id: String,
        )

        assertEquals(fake<GeneratedLocalUser>(seed = 123).id, fake<GeneratedLocalUser>(seed = 123).id)
    }

    @Test
    fun `compiler plugin skips private constructors by default`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedPrivateConstructorUser>(seed = 123)
        }
    }

    @Test
    fun `explicit rules can generate classes with private constructors`() {
        val fiktion =
            Fiktion {
                type<GeneratedPrivateConstructorUser>() generates GeneratedPrivateConstructorUser.create(id = "configured")
            }

        assertEquals("configured", fiktion.fake<GeneratedPrivateConstructorUser>(seed = 123).id)
    }

    @Test
    fun `compiler plugin uses configured factory methods for private constructors`() {
        val fiktion =
            Fiktion {
                type<GeneratedFactoryConstructorUser>() constructsBy GeneratedFactoryConstructorUser::create
                property<GeneratedFactoryConstructorUser, String>("roleName") generates "admin"
            }
        val user = fiktion.fake<GeneratedFactoryConstructorUser>(seed = 123)

        assertEquals(fiktion.fake<GeneratedFactoryConstructorUser>(seed = 123).id, user.id)
        assertEquals(GeneratedFactoryRole.Admin, user.role)
    }

    @Test
    fun `compiler plugin registers generated metadata when factory methods are configured`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, false)

            Fiktion {
                type<GeneratedFactoryConstructorUser>() constructsBy GeneratedFactoryConstructorUser::create
            }

            assertTrue(generatedMetadata.get().isNotEmpty())
            assertTrue(guard.getBoolean(null))
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
        }
    }

    @Test
    fun `compiler plugin registers generated metadata when factory methods are globally configured`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, false)

            val snapshot =
                Fiktion.configure {
                    type<GeneratedFactoryConstructorUser>() constructsBy GeneratedFactoryConstructorUser::create
                }
            try {
                assertTrue(generatedMetadata.get().isNotEmpty())
                assertTrue(guard.getBoolean(null))
            } finally {
                assertTrue(snapshot.restore())
            }
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
        }
    }

    @Test
    fun `compiler plugin registers generated metadata when factory methods are installed from addons`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, false)

            Fiktion {
                install(GeneratedFactoryConstructorTestAddon)
            }

            assertTrue(generatedMetadata.get().isNotEmpty())
            assertTrue(guard.getBoolean(null))
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
        }
    }

    @Test
    fun `compiler plugin registers generated metadata when factory methods are globally installed from addons`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, false)

            val snapshot =
                Fiktion.configure {
                    install(GeneratedFactoryConstructorTestAddon)
                }
            try {
                assertTrue(generatedMetadata.get().isNotEmpty())
                assertTrue(guard.getBoolean(null))
            } finally {
                assertTrue(snapshot.restore())
            }
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
        }
    }

    @Test
    fun `compiler plugin registers generated metadata from another module before fake calls`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, false)
            resetExternalFactoryConstructorRegistrarGuard(false)

            val user = fake<ExternalFactoryConstructorUser>(seed = 123)

            assertEquals(ExternalFactoryRole.Admin, user.role)
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
            resetExternalFactoryConstructorRegistrarGuard(false)
        }
    }

    @Test
    fun `compiler plugin uses nullable factory methods for nullable targets`() {
        val fiktion =
            Fiktion {
                type<GeneratedNullableFactoryConstructorUser?>() constructsBy GeneratedNullableFactoryConstructorUser::createOrNull
                type<GeneratedNullableFactoryConstructorUser?>() generates auto orNullAt 0.0
                property<GeneratedNullableFactoryConstructorUser?, String>("id") generates "missing"
            }

        assertNull(fiktion.fake<GeneratedNullableFactoryConstructorUser?>(seed = 123))
        assertFailsWith<CannotGenerateException> {
            fiktion.fake<GeneratedNullableFactoryConstructorUser>(seed = 123)
        }
    }

    @Test
    fun `compiler plugin uses explicitly typed overloaded factory methods`() {
        val create: KFunction2<String, String, GeneratedOverloadedFactoryConstructorUser> =
            GeneratedOverloadedFactoryConstructorUser::create
        val fiktion =
            Fiktion {
                type<GeneratedOverloadedFactoryConstructorUser>() constructsBy create
                property<GeneratedOverloadedFactoryConstructorUser, String>("suffix") generates "configured"
            }
        val user = fiktion.fake<GeneratedOverloadedFactoryConstructorUser>(seed = 123)

        assertEquals("configured", user.suffix)
    }

    @Test
    fun `compiler plugin uses configured top-level factory methods`() {
        val fiktion =
            Fiktion {
                type<GeneratedTopLevelFactoryConstructorUser>() constructsBy ::createGeneratedTopLevelFactoryConstructorUser
            }
        val user = fiktion.fake<GeneratedTopLevelFactoryConstructorUser>(seed = 123)

        assertEquals(GeneratedFactoryRole.Admin, user.role)
    }

    @Test
    fun `compiler plugin uses factory parameter defaults`() {
        val fiktion =
            Fiktion {
                type<GeneratedDefaultFactoryConstructorUser>() constructsBy GeneratedDefaultFactoryConstructorUser::create
                property<GeneratedDefaultFactoryConstructorUser, String>("suffix") generates default
            }
        val user = fiktion.fake<GeneratedDefaultFactoryConstructorUser>(seed = 123)

        assertEquals("default", user.suffix)
    }

    @Test
    fun `compiler plugin skips protected constructors by default`() {
        assertFailsWith<CannotGenerateException> {
            fake<GeneratedProtectedConstructorUser>(seed = 123)
        }
    }

    @Test
    fun `explicit rules can generate classes with protected constructors`() {
        val fiktion =
            Fiktion {
                type<GeneratedProtectedConstructorUser>() generates GeneratedProtectedConstructorUser.create(id = "configured")
            }

        assertEquals("configured", fiktion.fake<GeneratedProtectedConstructorUser>(seed = 123).id)
    }

    @Test
    fun `compiler plugin registers generated metadata for internal constructors`() {
        val user = fake<GeneratedInternalConstructorUser>(seed = 123)

        assertEquals(fake<GeneratedInternalConstructorUser>(seed = 123).id, user.id)
    }

    @Test
    fun `compiler plugin registers generated metadata for nested fake calls`() {
        assertEquals(fake<GeneratedUserWrapper>(seed = 123), fake<GeneratedUserWrapper>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for function constructor properties`() {
        val user = fake<GeneratedFunctionPropertyUser>(seed = 123)

        assertTrue(user.callback(1, "ignored").isNotBlank())
    }

    @Test
    fun `compiler plugin uses static constructor defaults and generated values`() {
        val defaultFiktion =
            Fiktion {
                GeneratedUserWithStaticDefault::id generates default
            }
        val generatedFiktion =
            Fiktion {
                GeneratedUserWithStaticDefault::id generates "generated-id"
            }

        assertEquals(GeneratedUserWithStaticDefault(id = "static-id"), defaultFiktion.fake(seed = 0))
        assertEquals(GeneratedUserWithStaticDefault(id = "generated-id"), generatedFiktion.fake(seed = 0))
    }

    @Test
    fun `compiler plugin uses null constructor defaults`() {
        val fiktion =
            Fiktion {
                GeneratedUserWithNullDefault::id generates default
            }

        assertEquals(GeneratedUserWithNullDefault(id = null), fiktion.fake(seed = 0))
    }

    @Test
    fun `compiler plugin evaluates dynamic constructor defaults`() {
        dynamicDefaultIndex = 0
        val defaultFiktion =
            Fiktion {
                GeneratedUserWithDynamicDefault::id generates default
            }

        assertEquals(GeneratedUserWithDynamicDefault(id = "dynamic-id-0"), defaultFiktion.fake(seed = 0))
        assertEquals(GeneratedUserWithDynamicDefault(id = "dynamic-id-1"), defaultFiktion.fake(seed = 1))
        assertTrue(fake<GeneratedUserWithDynamicDefault>(seed = 3).id.startsWith("dynamic-id-").not())
    }

    @Test
    fun `compiler plugin supports split default decisions`() {
        dynamicDefaultIndex = 0
        val defaultName =
            Fiktion {
                GeneratedUserWithMultipleDefaults::name generates default
                GeneratedUserWithMultipleDefaults::label generates "generated-label"
            }.fake<GeneratedUserWithMultipleDefaults>(seed = 0)
        val defaultLabel =
            Fiktion {
                GeneratedUserWithMultipleDefaults::name generates "generated-name"
                GeneratedUserWithMultipleDefaults::label generates default
            }.fake<GeneratedUserWithMultipleDefaults>(seed = 0)

        assertEquals("static-name", defaultName.name)
        assertEquals("generated-label", defaultName.label)
        assertEquals("generated-name", defaultLabel.name)
        assertTrue(defaultLabel.label.startsWith("dynamic-id-"))
    }

    @Test
    fun `compiler plugin evaluates constructor defaults that reference other properties`() {
        val user =
            Fiktion {
                GeneratedUserWithPropertyDefault::label generates default
            }.fake<GeneratedUserWithPropertyDefault>(seed = 0)

        assertEquals("label-${user.id}", user.label)
    }

    @Test
    fun `compiler plugin registers generated metadata for value classes`() {
        assertEquals(fake<GeneratedUserId>(seed = 123), fake<GeneratedUserId>(seed = 123))
        assertTrue(fake<GeneratedUserId>(seed = 123).value.isNotBlank())
    }

    @Test
    fun `compiler plugin registers generated metadata for generic value classes`() {
        val fiktion =
            Fiktion {
                type<Int>() generates 42
                this using FiktionConfig.Collection.size(1)
            }
        val value = fiktion.fake<GeneratedValueList<Int>>(seed = 123)

        assertEquals(listOf(42), value.values)
    }

    @Test
    fun `compiler plugin registers generated metadata with value class property names`() {
        val userId =
            fake<GeneratedUserId>(seed = 123) {
                GeneratedUserId::value generates "configured"
            }

        assertEquals(GeneratedUserId("configured"), userId)
    }

    @Test
    fun `compiler plugin registers generated metadata with private value class property names`() {
        val userId =
            fake<GeneratedPrivateUserId>(seed = 123) {
                name("value") generates "configured"
            }

        assertEquals(GeneratedPrivateUserId("configured"), userId)
    }

    @Test
    fun `type rules override generated value classes`() {
        val fiktion =
            Fiktion {
                type<GeneratedUserId>() generates GeneratedUserId("configured")
            }

        assertEquals(GeneratedUserId("configured"), fiktion.fake<GeneratedUserId>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for enum classes`() {
        assertEquals(fake<GeneratedStatus>(seed = 123), fake<GeneratedStatus>(seed = 123))
        assertTrue(fake<GeneratedStatus>(seed = 123) in GeneratedStatus.entries)
    }

    @Test
    fun `compiler plugin registers generated metadata for sealed classes`() {
        val message = fake<GeneratedMessage>(seed = 123)

        assertEquals(message, fake<GeneratedMessage>(seed = 123))
        assertTrue(message is GeneratedTextMessage || message is GeneratedImageMessage || message is GeneratedLoadingMessage)
    }

    @Test
    fun `compiler plugin registers generated metadata for sealed interfaces`() {
        val event = fake<GeneratedEvent>(seed = 123)

        assertEquals(event, fake<GeneratedEvent>(seed = 123))
        assertTrue(event is GeneratedCreatedEvent || event is GeneratedDeletedEvent)
    }

    @Test
    fun `compiler plugin flattens nested sealed metadata leaves`() {
        val notification = fake<GeneratedNotification>(seed = 123)

        assertEquals(notification, fake<GeneratedNotification>(seed = 123))
        assertTrue(notification is GeneratedEmailNotification || notification is GeneratedIdleNotification)
    }

    @Test
    fun `compiler plugin registers generated metadata for singleton objects`() {
        assertEquals(GeneratedLoadingMessage, fake<GeneratedLoadingMessage>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for companion objects`() {
        assertEquals(GeneratedCompanionOwner, fake<GeneratedCompanionOwner.Companion>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for arrays`() {
        val strings = fake<Array<String>>(seed = 123)
        val users = fake<Array<GeneratedRegularUser>>(seed = 123)

        assertEquals(fake<Array<String>>(seed = 123).toList(), strings.toList())
        assertEquals(fake<Array<GeneratedRegularUser>>(seed = 123).map { user -> user.id }, users.map { user -> user.id })
        assertTrue(strings.isNotEmpty())
        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `compiler plugin registers generated metadata for nested arrays`() {
        val strings = fake<Array<Array<String>>>(seed = 123)
        val users = fake<Array<Array<GeneratedRegularUser>>>(seed = 123)

        assertEquals(fake<Array<Array<String>>>(seed = 123).map { value -> value.toList() }, strings.map { value -> value.toList() })
        assertEquals(
            fake<Array<Array<GeneratedRegularUser>>>(seed = 123).map { value -> value.map { user -> user.id } },
            users.map { value -> value.map { user -> user.id } },
        )
        assertTrue(strings.isNotEmpty())
        assertTrue(strings.all { value -> value.isNotEmpty() })
        assertTrue(users.isNotEmpty())
        assertTrue(users.all { value -> value.isNotEmpty() })
    }

    @Test
    fun `compiler plugin registers generated metadata for arrays inside generic types`() {
        val list = fake<List<Array<String>>>(seed = 123)
        val map = fake<Map<String, Array<Int>>>(seed = 123)
        val pair = fake<Pair<Array<String>, Int>>(seed = 123)

        assertTrue(list.isNotEmpty())
        assertTrue(list.all { value -> value.isNotEmpty() })
        assertTrue(map.isNotEmpty())
        assertTrue(map.values.all { value -> value.isNotEmpty() })
        assertTrue(pair.first.isNotEmpty())
    }

    @Test
    fun `compiler plugin registers generated metadata for array constructor properties`() {
        val user = fake<GeneratedArrayPropertyUser>(seed = 123)
        val nestedUser = fake<GeneratedNestedArrayPropertyUser>(seed = 123)

        assertTrue(user.ids.isNotEmpty())
        assertTrue(nestedUser.ids.isNotEmpty())
        assertTrue(nestedUser.ids.all { ids -> ids.isNotEmpty() })
    }

    @Test
    fun `compiler plugin registers generated metadata for arrays inside generic constructor properties`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Collection.size(1)
                this using FiktionConfig.Map.size(1)
            }
        val user = fiktion.fake<GeneratedGenericArrayPropertyUser>(seed = 123)

        assertTrue(user.ids.isNotEmpty())
        assertTrue(user.ids.all { ids -> ids.isNotEmpty() })
        assertTrue(user.scores.isNotEmpty())
        assertTrue(user.scores.values.all { scores -> scores.isNotEmpty() })
    }

    @Test
    fun `compiler plugin generated metadata can initialize from generated classes`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, true)
            assertEquals(fake<GeneratedGuardedUser>(seed = 123), fake<GeneratedGuardedUser>(seed = 123))

            guard.setBoolean(null, false)
            assertEquals(fake<GeneratedGuardedUser>(seed = 123), fake<GeneratedGuardedUser>(seed = 123))
            assertTrue(guard.getBoolean(null))
        } finally {
            generatedMetadata.set(previousMetadata)
            guard.setBoolean(null, previous)
        }
    }
}

/**
 * Smoke-test model that depends on compiler-generated metadata.
 */
private data class GeneratedUser(
    /**
     * User identifier generated from built-in String generation.
     */
    val id: String,
)

/**
 * Smoke-test model for property rules that depend on compiler-generated metadata.
 */
private data class GeneratedDependentUser(
    /**
     * User identifier used by another generated property.
     */
    val id: String,
    /**
     * Email generated from [id].
     */
    val email: String,
)

/**
 * Smoke-test model for property rules with multiple dependency types.
 */
private data class GeneratedDependentUserLabel(
    /**
     * User identifier used by a dependent label.
     */
    val id: String,
    /**
     * User score used by a dependent label.
     */
    val score: Int,
    /**
     * Label generated from [id] and [score].
     */
    val label: String,
)

/**
 * Smoke-test model with an array constructor property.
 */
private data class GeneratedArrayPropertyUser(
    /**
     * Generated user identifiers.
     */
    val ids: Array<String>,
)

/**
 * Smoke-test model with a nested array constructor property.
 */
private data class GeneratedNestedArrayPropertyUser(
    /**
     * Generated nested user identifiers.
     */
    val ids: Array<Array<String>>,
)

/**
 * Smoke-test model with array constructor properties inside generic types.
 */
private data class GeneratedGenericArrayPropertyUser(
    /**
     * Generated user identifiers.
     */
    val ids: List<Array<String>>,
    /**
     * Generated user scores.
     */
    val scores: Map<String, Array<Int>>,
)

/**
 * Smoke-test regular class that depends on compiler-generated metadata.
 */
private class GeneratedRegularUser(
    /**
     * Regular user identifier generated from built-in String generation.
     */
    val id: String,
)

/**
 * Smoke-test regular class with a private constructor property.
 */
private class GeneratedRegularUserWithPrivateProperty(
    /**
     * Private regular user identifier generated from constructor property metadata.
     */
    private val id: String,
) {
    /**
     * Returns the private identifier for assertions.
     */
    fun id(): String = id
}

/**
 * Smoke-test regular class with a non-property constructor parameter.
 */
private class GeneratedRegularUserWithConstructorParameter(
    /**
     * Regular user identifier accepted as a constructor argument.
     */
    id: String,
) {
    /**
     * Identifier copied from the constructor parameter.
     */
    val id: String = id
}

/**
 * Smoke-test regular class with constructor property type arguments.
 */
private data class GeneratedRegularUserWithProfiles(
    /**
     * User profiles generated through collection element metadata.
     */
    val profiles: List<GeneratedRegularUserProfile>,
)

/**
 * Smoke-test model generated only through constructor property type arguments.
 */
private data class GeneratedRegularUserProfile(
    /**
     * Required profile text.
     */
    val bio: String,
)

/**
 * Smoke-test generic interface bound.
 */
private interface GeneratedBox<T>

/**
 * Smoke-test concrete generic interface implementation.
 */
private data class GeneratedBoxImpl<T>(
    /**
     * Generated boxed value.
     */
    val value: T,
) : GeneratedBox<T>

/**
 * Smoke-test generic class whose constructor property references its type parameter.
 */
private data class GeneratedHolder<B : GeneratedBox<String>>(
    /**
     * Generated generic box.
     */
    val box: B,
)

/**
 * Smoke-test regular class whose primary constructor should be selected over secondary constructors.
 */
private class GeneratedRegularUserWithSecondaryConstructor(
    /**
     * Regular user identifier generated from built-in String generation.
     */
    val id: String,
) {
    /**
     * Marker showing which constructor initialized this instance.
     */
    val source: String = "primary"

    /**
     * Secondary constructor that should not be used by generated metadata.
     */
    constructor() : this("secondary")
}

/**
 * Smoke-test regular class that has no primary constructor.
 */
private class GeneratedRegularUserWithoutPrimaryConstructor {
    /**
     * Regular user identifier initialized by a secondary constructor.
     */
    val id: String

    /**
     * Secondary constructor that generated metadata should not target.
     */
    constructor(id: String) {
        this.id = id
    }
}

/**
 * Smoke-test regular class with a vararg primary constructor.
 */
private class GeneratedVarargConstructorUser(
    /**
     * Vararg user tags.
     */
    vararg val tags: String,
)

/**
 * Smoke-test abstract class that generated metadata should not target.
 */
private abstract class GeneratedAbstractUser(
    /**
     * Abstract user identifier.
     */
    val id: String,
)

/**
 * Smoke-test interface that generated metadata should not target.
 */
private interface GeneratedUserContract {
    /**
     * Contract user identifier.
     */
    val id: String
}

/**
 * Smoke-test fun interface that generated metadata should not target.
 */
private fun interface GeneratedUserFactory {
    /**
     * Creates a user identifier.
     */
    fun create(): String
}

/**
 * Smoke-test annotation class that generated metadata should not target.
 */
private annotation class GeneratedUserAnnotation(
    /**
     * Annotation user identifier.
     */
    val id: String,
)

/**
 * Smoke-test outer class containing an inner class that generated metadata should not target.
 */
private class GeneratedOuterUser {
    /**
     * Smoke-test inner class that generated metadata should not target.
     */
    inner class GeneratedInnerUser(
        /**
         * Inner user identifier.
         */
        val id: String,
    )
}

/**
 * Smoke-test class whose private constructor should not receive generated metadata.
 */
private class GeneratedPrivateConstructorUser private constructor(
    /**
     * Private-constructor user identifier.
     */
    val id: String,
) {
    /**
     * Factory for tests that explicitly define how to create this type.
     */
    companion object {
        /**
         * Creates a private-constructor user for explicit rule tests.
         */
        fun create(id: String): GeneratedPrivateConstructorUser = GeneratedPrivateConstructorUser(id)
    }
}

/**
 * Smoke-test class generated through an explicitly configured factory method.
 */
private class GeneratedFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
    /**
     * Factory-created user role.
     */
    val role: GeneratedFactoryRole,
) {
    /**
     * Factory for generated metadata tests.
     */
    companion object {
        /**
         * Creates a user and converts a differently named factory parameter.
         */
        fun create(
            id: String,
            roleName: String,
        ): GeneratedFactoryConstructorUser =
            GeneratedFactoryConstructorUser(
                id = id,
                role = GeneratedFactoryRole.valueOf(roleName.replaceFirstChar(Char::uppercase)),
            )
    }
}

/**
 * Smoke-test class generated through an automatic add-on factory declaration.
 */
private class GeneratedAddonFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
    /**
     * Converted factory-created role.
     */
    val role: GeneratedFactoryRole,
) {
    companion object {
        /**
         * Creates a user and converts a differently named factory parameter.
         */
        fun create(
            id: String,
            roleName: String,
        ): GeneratedAddonFactoryConstructorUser =
            GeneratedAddonFactoryConstructorUser(
                id = id,
                role = GeneratedFactoryRole.valueOf(roleName.replaceFirstChar { char -> char.uppercase() }),
            )
    }
}

/**
 * Smoke-test add-on that contributes factory metadata.
 */
private object GeneratedFactoryConstructorTestAddon : FiktionAddon {
    override val id: String = "dev.s7a.fiktion.compiler.generatedFactoryConstructorTestAddon"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<GeneratedAddonFactoryConstructorUser>() constructsBy GeneratedAddonFactoryConstructorUser::create
            property<GeneratedAddonFactoryConstructorUser, String>("roleName") generates "admin"
        }
    }
}

/**
 * Smoke-test role converted inside a factory method.
 */
private enum class GeneratedFactoryRole {
    /**
     * Administrator role.
     */
    Admin,
}

/**
 * Smoke-test class generated through a nullable factory method.
 */
private class GeneratedNullableFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
) {
    /**
     * Factory for nullable generated metadata tests.
     */
    companion object {
        /**
         * Creates a user unless [id] asks the factory to return null.
         */
        fun createOrNull(id: String): GeneratedNullableFactoryConstructorUser? =
            if (id == "missing") {
                null
            } else {
                GeneratedNullableFactoryConstructorUser(id)
            }
    }
}

/**
 * Smoke-test class generated through an overloaded factory method.
 */
private class GeneratedOverloadedFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
    /**
     * Factory-created suffix proving the selected overload.
     */
    val suffix: String,
) {
    /**
     * Overloaded factories for generated metadata tests.
     */
    companion object {
        /**
         * Creates a user with a default suffix.
         */
        fun create(id: String): GeneratedOverloadedFactoryConstructorUser =
            GeneratedOverloadedFactoryConstructorUser(id = id, suffix = "default")

        /**
         * Creates a user with an explicit suffix.
         */
        fun create(
            id: String,
            suffix: String,
        ): GeneratedOverloadedFactoryConstructorUser = GeneratedOverloadedFactoryConstructorUser(id = id, suffix = suffix)
    }
}

/**
 * Smoke-test class generated through a top-level factory method.
 */
private class GeneratedTopLevelFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
    /**
     * Factory-created user role.
     */
    val role: GeneratedFactoryRole,
) {
    /**
     * Production-style factory used by the test-code top-level factory.
     */
    companion object {
        /**
         * Creates a user and converts a differently typed factory parameter.
         */
        fun create(
            id: String,
            roleName: String,
        ): GeneratedTopLevelFactoryConstructorUser =
            GeneratedTopLevelFactoryConstructorUser(
                id = id,
                role = GeneratedFactoryRole.valueOf(roleName.replaceFirstChar(Char::uppercase)),
            )
    }
}

private fun createGeneratedTopLevelFactoryConstructorUser(
    id: String,
    role: GeneratedFactoryRole,
): GeneratedTopLevelFactoryConstructorUser =
    GeneratedTopLevelFactoryConstructorUser.create(
        id = id,
        roleName = role.name.lowercase(),
    )

/**
 * Smoke-test class generated through a factory method with a default parameter.
 */
private class GeneratedDefaultFactoryConstructorUser private constructor(
    /**
     * Factory-created user identifier.
     */
    val id: String,
    /**
     * Factory-created suffix.
     */
    val suffix: String,
) {
    /**
     * Factory for default-parameter generated metadata tests.
     */
    companion object {
        /**
         * Creates a user with an optional suffix.
         */
        fun create(
            id: String,
            suffix: String = "default",
        ): GeneratedDefaultFactoryConstructorUser =
            GeneratedDefaultFactoryConstructorUser(
                id = id,
                suffix = suffix,
            )
    }
}

/**
 * Smoke-test class whose protected constructor should not receive generated metadata.
 */
private open class GeneratedProtectedConstructorUser protected constructor(
    /**
     * Protected-constructor user identifier.
     */
    val id: String,
) {
    /**
     * Factory for tests that explicitly define how to create this type.
     */
    companion object {
        /**
         * Creates a protected-constructor user for explicit rule tests.
         */
        fun create(id: String): GeneratedProtectedConstructorUser = GeneratedProtectedConstructorUser(id)
    }
}

/**
 * Smoke-test class whose internal constructor should receive generated metadata.
 */
private class GeneratedInternalConstructorUser internal constructor(
    /**
     * Internal-constructor user identifier.
     */
    val id: String,
)

/**
 * Smoke-test model that depends on nested compiler-generated metadata.
 */
private data class GeneratedUserWrapper(
    /**
     * Generated user value.
     */
    val user: GeneratedUser,
)

/**
 * Smoke-test model with a function constructor property.
 */
private data class GeneratedFunctionPropertyUser(
    /**
     * Generated callback function.
     */
    val callback: (Int, String) -> String,
)

/**
 * Smoke-test model with a static constructor default.
 */
private data class GeneratedUserWithStaticDefault(
    /**
     * User identifier that can use a static default.
     */
    val id: String = "static-id",
)

/**
 * Smoke-test model with a null constructor default.
 */
private data class GeneratedUserWithNullDefault(
    /**
     * Optional user identifier that can use a null default.
     */
    val id: String? = null,
)

/**
 * Smoke-test model with a dynamic constructor default.
 */
private data class GeneratedUserWithDynamicDefault(
    /**
     * User identifier that can use a dynamic default.
     */
    val id: String = nextDynamicDefaultId(),
)

/**
 * Smoke-test model with several defaultable constructor parameters.
 */
private data class GeneratedUserWithMultipleDefaults(
    /**
     * Required user identifier.
     */
    val id: String,
    /**
     * User name with a static default.
     */
    val name: String = "static-name",
    /**
     * User label with a dynamic default.
     */
    val label: String = nextDynamicDefaultId(),
)

/**
 * Smoke-test model with a constructor default that reads another constructor property.
 */
private data class GeneratedUserWithPropertyDefault(
    /**
     * Required user identifier.
     */
    val id: String,
    /**
     * Label derived from [id] by the Kotlin default argument expression.
     */
    val label: String = "label-$id",
)

/**
 * Smoke-test value class that depends on compiler-generated metadata.
 */
@JvmInline
private value class GeneratedUserId(
    /**
     * Underlying user identifier.
     */
    val value: String,
)

/**
 * Smoke-test value class with a private underlying property.
 */
@JvmInline
private value class GeneratedPrivateUserId(
    /**
     * Private underlying user identifier.
     */
    private val value: String,
)

/**
 * Smoke-test generic value class that depends on compiler-generated metadata.
 */
@JvmInline
private value class GeneratedValueList<out T>(
    /**
     * Underlying generated values.
     */
    val values: List<T>,
) : List<T> by values

/**
 * Smoke-test enum that depends on compiler-generated metadata.
 */
private enum class GeneratedStatus {
    /**
     * Active generated status.
     */
    ACTIVE,

    /**
     * Deleted generated status.
     */
    DELETED,
}

/**
 * Smoke-test sealed type that depends on compiler-generated metadata.
 */
private sealed class GeneratedMessage

/**
 * Smoke-test sealed subtype containing text.
 */
private data class GeneratedTextMessage(
    /**
     * Generated text.
     */
    val text: String,
) : GeneratedMessage()

/**
 * Smoke-test sealed subtype containing a URL.
 */
private data class GeneratedImageMessage(
    /**
     * Generated image URL.
     */
    val url: String,
) : GeneratedMessage()

/**
 * Smoke-test sealed singleton subtype.
 */
private data object GeneratedLoadingMessage : GeneratedMessage()

/**
 * Smoke-test sealed interface that depends on compiler-generated metadata.
 */
private sealed interface GeneratedEvent

/**
 * Smoke-test sealed interface subtype for creation.
 */
private data class GeneratedCreatedEvent(
    /**
     * Generated creation identifier.
     */
    val id: String,
) : GeneratedEvent

/**
 * Smoke-test sealed interface subtype for deletion.
 */
private data class GeneratedDeletedEvent(
    /**
     * Generated deletion identifier.
     */
    val id: String,
) : GeneratedEvent

/**
 * Smoke-test nested sealed interface root.
 */
private sealed interface GeneratedNotification

/**
 * Smoke-test nested sealed interface branch.
 */
private sealed interface GeneratedSystemNotification : GeneratedNotification

/**
 * Smoke-test nested sealed data leaf.
 */
private data class GeneratedEmailNotification(
    /**
     * Generated email subject.
     */
    val subject: String,
) : GeneratedSystemNotification

/**
 * Smoke-test nested sealed singleton leaf.
 */
private data object GeneratedIdleNotification : GeneratedSystemNotification

/**
 * Smoke-test class containing a companion object.
 */
private class GeneratedCompanionOwner {
    /**
     * Smoke-test companion object that depends on compiler-generated metadata.
     */
    companion object
}

/**
 * Smoke-test model used to verify the generated registrar guard.
 */
private data class GeneratedGuardedUser(
    /**
     * Generated guarded user identifier.
     */
    val id: String,
)

/**
 * Counter used to prove dynamic defaults are evaluated at construction time.
 */
private var dynamicDefaultIndex: Int = 0

/**
 * Returns a new dynamic default identifier.
 */
private fun nextDynamicDefaultId(): String = "dynamic-id-${dynamicDefaultIndex++}"

/**
 * Returns the compiler-generated registrar guard field.
 */
private fun generatedRegistrarGuardField(): java.lang.reflect.Field =
    Class
        .forName("dev.s7a.fiktion.compiler.GeneratedMetadataSmokeTestKt")
        .declaredFields
        .single { field -> field.name.contains("fiktionGeneratedMetadataRegistered") }
        .also { field -> field.isAccessible = true }

/**
 * Returns the global generated metadata reference.
 */
@Suppress("UNCHECKED_CAST")
private fun generatedMetadataReference(): AtomicReference<Map<String, Any?>> =
    Class
        .forName("dev.s7a.fiktion.GlobalFiktion")
        .getDeclaredField("generatedMetadata")
        .also { field -> field.isAccessible = true }
        .get(null) as AtomicReference<Map<String, Any?>>
