package dev.s7a.fiktion

import kotlin.jvm.JvmInline

/**
 * Test value class used for value metadata generation.
 */
@JvmInline
internal value class UserId(
    /**
     * Underlying identifier value.
     */
    val value: String,
)
