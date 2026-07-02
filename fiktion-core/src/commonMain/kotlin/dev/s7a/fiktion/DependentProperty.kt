package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Stable identity for a direct property dependency.
 */
internal data class DependentProperty(
    /**
     * Type that owns the dependency property.
     */
    val owner: KType,
    /**
     * Dependency property name.
     */
    val name: String,
    /**
     * Dependency property value type, when the declaration preserved it.
     */
    val value: KType?,
)
