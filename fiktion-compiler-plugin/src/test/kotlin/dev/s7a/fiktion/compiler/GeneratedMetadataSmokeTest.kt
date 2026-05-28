package dev.s7a.fiktion.compiler

import dev.s7a.fiktion.fake
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedMetadataSmokeTest {
    @Test
    fun `compiler plugin registers generated metadata before fake calls`() {
        assertEquals(fake<GeneratedUser>(seed = 123), fake<GeneratedUser>(seed = 123))
    }

    @Test
    fun `compiler plugin registers generated metadata for nested fake calls`() {
        assertEquals(fake<GeneratedUserWrapper>(seed = 123), fake<GeneratedUserWrapper>(seed = 123))
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
