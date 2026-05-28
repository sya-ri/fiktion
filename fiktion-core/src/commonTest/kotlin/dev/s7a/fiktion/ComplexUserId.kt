package dev.s7a.fiktion

import kotlin.jvm.JvmInline

/**
 * Test value class with a non-built-in underlying value.
 */
@JvmInline
value class ComplexUserId(
    /**
     * Underlying complex user identifier value.
     */
    val value: ComplexUserIdValue,
)
