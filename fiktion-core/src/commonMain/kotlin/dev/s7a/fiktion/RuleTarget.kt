package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Target selected by a rule declaration.
 */
public interface RuleTarget<T>

/**
 * Generates [value] for this rule target.
 */
public infix fun <T> RuleTarget<T>.generates(value: T): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates values for this rule target by invoking [generator].
 */
public infix fun <T> RuleTarget<T>.generatesBy(generator: Generator<T>): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates comparable values within [range].
 */
public infix fun <T : Comparable<T>> RuleTarget<T>.generatesIn(range: ClosedRange<T>): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates integer values within [range].
 */
public infix fun RuleTarget<Int>.generatesIn(range: IntRange): GenerationSpec<Int> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates long values within [range].
 */
public infix fun RuleTarget<Long>.generatesIn(range: LongRange): GenerationSpec<Long> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates one value from [values].
 */
public infix fun <T> RuleTarget<T>.generatesOneOf(values: Iterable<T>): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Uses automatic generation for this rule target.
 */
public fun <T> RuleTarget<T>.autoGenerates(): GenerationSpec<T> = throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Generates each element for this collection rule target by invoking [generator].
 */
public infix fun <Element, CollectionType : Collection<Element>> RuleTarget<CollectionType>.generatesEach(
    generator: Generator<Element>,
): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")
