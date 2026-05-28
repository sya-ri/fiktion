package dev.s7a.fiktion

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Target selected by a property-name rule before its value type is known.
 */
public sealed interface RuleNameTarget {
    /**
     * Generates values for this name target using [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified generates or generatesBy overload.", level = DeprecationLevel.ERROR)
    public fun generates(
        type: KType,
        generator: FakeContext.() -> Any?,
    ): GenerationSpec<*>
}

/**
 * Generates [value] for this name target and infers the target value type from [value].
 */
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline infix fun <reified T> RuleNameTarget.generates(value: T): GenerationSpec<T> =
    generates(typeOf<T>()) { value } as GenerationSpec<T>

/**
 * Generates values for this name target and infers the target value type from [generator].
 */
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline infix fun <reified T> RuleNameTarget.generatesBy(noinline generator: Generator<T>): GenerationSpec<T> =
    generates(typeOf<T>(), generator) as GenerationSpec<T>
