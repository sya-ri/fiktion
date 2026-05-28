package dev.s7a.fiktion.compiler

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.runtime.CannotGenerateException
import dev.s7a.fiktion.type
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    fun `compiler plugin skips local classes`() {
        class GeneratedLocalUser(
            /**
             * Local user identifier.
             */
            val id: String,
        )

        assertFailsWith<CannotGenerateException> {
            fake<GeneratedLocalUser>(seed = 123)
        }
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
    fun `compiler plugin uses static constructor defaults and generated values`() {
        assertEquals(GeneratedUserWithStaticDefault(id = "static-id"), fake<GeneratedUserWithStaticDefault>(seed = 0))
        assertTrue(fake<GeneratedUserWithStaticDefault>(seed = 3).id != "static-id")
    }

    @Test
    fun `compiler plugin uses null constructor defaults`() {
        assertEquals(GeneratedUserWithNullDefault(id = null), fake<GeneratedUserWithNullDefault>(seed = 0))
    }

    @Test
    fun `compiler plugin evaluates dynamic constructor defaults`() {
        dynamicDefaultIndex = 0

        assertEquals(GeneratedUserWithDynamicDefault(id = "dynamic-id-0"), fake<GeneratedUserWithDynamicDefault>(seed = 0))
        assertEquals(GeneratedUserWithDynamicDefault(id = "dynamic-id-1"), fake<GeneratedUserWithDynamicDefault>(seed = 1))
        assertTrue(fake<GeneratedUserWithDynamicDefault>(seed = 3).id.startsWith("dynamic-id-").not())
    }

    @Test
    fun `compiler plugin supports split default decisions`() {
        dynamicDefaultIndex = 0
        val generatedName = fake<GeneratedUserWithMultipleDefaults>(seed = 2)
        val defaultName = fake<GeneratedUserWithMultipleDefaults>(seed = 3)

        assertTrue(generatedName.name != "static-name")
        assertTrue(generatedName.label.startsWith("dynamic-id-"))
        assertEquals("static-name", defaultName.name)
        assertTrue(defaultName.label.startsWith("dynamic-id-").not())
    }

    @Test
    fun `compiler plugin evaluates constructor defaults that reference other properties`() {
        val user = fake<GeneratedUserWithPropertyDefault>(seed = 0)

        assertEquals("label-${user.id}", user.label)
    }

    @Test
    fun `compiler plugin registers generated metadata for value classes`() {
        assertEquals(fake<GeneratedUserId>(seed = 123), fake<GeneratedUserId>(seed = 123))
        assertTrue(fake<GeneratedUserId>(seed = 123).value.isNotBlank())
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
    fun `compiler plugin generated registrar uses once guard`() {
        val guard = generatedRegistrarGuardField()
        val generatedMetadata = generatedMetadataReference()
        val previous = guard.getBoolean(null)
        val previousMetadata = generatedMetadata.get()
        try {
            generatedMetadata.set(emptyMap())
            guard.setBoolean(null, true)
            assertFailsWith<CannotGenerateException> {
                fake<GeneratedGuardedUser>(seed = 123)
            }

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
 * Smoke-test regular class that depends on compiler-generated metadata.
 */
private class GeneratedRegularUser(
    /**
     * Regular user identifier generated from built-in String generation.
     */
    val id: String,
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
private fun generatedMetadataReference(): java.util.concurrent.atomic.AtomicReference<Map<String, Any?>> =
    Class
        .forName("dev.s7a.fiktion.GlobalFiktion")
        .getDeclaredField("generatedMetadata")
        .also { field -> field.isAccessible = true }
        .get(null) as java.util.concurrent.atomic.AtomicReference<Map<String, Any?>>
