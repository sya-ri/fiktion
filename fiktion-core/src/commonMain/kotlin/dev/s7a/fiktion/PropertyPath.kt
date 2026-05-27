package dev.s7a.fiktion

import kotlin.reflect.KProperty1
import kotlin.reflect.typeOf

/**
 * Type-safe path from [Root] to a nested [Value] property.
 *
 * Path matching uses non-null normalized type ids. This lets `/` traverse nullable intermediate properties while
 * matching the object shape rather than the nullability state at a particular step.
 */
public class PropertyPath<Root, out Value>
    @PublishedApi
    internal constructor(
        /**
         * Properties that make up this path.
         */
        public val properties: List<KProperty1<*, *>>,
        /**
         * Typed rule segments for matching this path.
         */
        @PublishedApi
        internal val segments: List<PathRuleSegment>,
    )

/**
 * Creates a nested property path using `/`.
 *
 * Nullable intermediate properties are accepted and normalized to their non-null value type for matching.
 */
public inline operator fun <reified Root, reified Intermediate : Any, reified Value> KProperty1<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> =
    PropertyPath(
        properties = listOf(this, next),
        segments =
            listOf(
                PathRuleSegment(
                    ownerId = typeOf<Root>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                    name = name,
                    valueId = typeOf<Intermediate>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
                PathRuleSegment(
                    ownerId = typeOf<Intermediate>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                    name = next.name,
                    valueId = typeOf<Value>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
            ),
    )

/**
 * Appends [next] to this property path.
 *
 * Nullable intermediate properties are accepted and normalized to their non-null value type for matching.
 */
public inline operator fun <Root, reified Intermediate : Any, reified Value> PropertyPath<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> =
    PropertyPath(
        properties = properties + next,
        segments =
            segments +
                PathRuleSegment(
                    ownerId = typeOf<Intermediate>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                    name = next.name,
                    valueId = typeOf<Value>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
    )
