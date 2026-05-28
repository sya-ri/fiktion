package dev.s7a.fiktion.compiler

import dev.s7a.fiktion.fake
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneratedMetadataSmokeTest {
    @Test
    fun `compiler plugin registers generated metadata before fake calls`() {
        assertEquals(fake<GeneratedUser>(seed = 123), fake<GeneratedUser>(seed = 123))
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
 * Counter used to prove dynamic defaults are evaluated at construction time.
 */
private var dynamicDefaultIndex: Int = 0

/**
 * Returns a new dynamic default identifier.
 */
private fun nextDynamicDefaultId(): String = "dynamic-id-${dynamicDefaultIndex++}"
