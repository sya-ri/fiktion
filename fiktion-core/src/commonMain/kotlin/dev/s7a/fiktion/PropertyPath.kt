package dev.s7a.fiktion

import kotlin.reflect.KProperty1

/**
 * Type-safe path from [Root] to a nested [Value] property.
 */
public class PropertyPath<Root, Value> internal constructor(
    /**
     * Properties that make up this path.
     */
    public val properties: List<KProperty1<*, *>>,
)

/**
 * Creates a nested property path using `/`.
 */
public operator fun <Root, Intermediate, Value> KProperty1<Root, Intermediate>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> = PropertyPath(listOf(this, next))

/**
 * Appends [next] to this property path.
 */
public operator fun <Root, Intermediate, Value> PropertyPath<Root, Intermediate>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> = PropertyPath(properties + next)
