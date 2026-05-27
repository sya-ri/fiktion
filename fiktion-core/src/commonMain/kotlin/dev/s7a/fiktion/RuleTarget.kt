package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Target selected by a rule declaration.
 */
public sealed interface RuleTarget<T>

/**
 * Generates [value] for this rule target.
 */
public infix fun <T> RuleTarget<T>.generates(value: T): GenerationSpec<T> = generatesBy { value }

/**
 * Generates values for this rule target by invoking [generator].
 */
public infix fun <T> RuleTarget<T>.generatesBy(generator: Generator<T>): GenerationSpec<T> =
    (this as DefaultRuleTarget<T>).generatesBy(generator)

/**
 * Generates integer values within [range].
 */
public infix fun RuleTarget<Int>.generatesIn(range: IntRange): GenerationSpec<Int> =
    generatesBy {
        require(!range.isEmpty()) { "range must not be empty." }
        range.random(random)
    }

/**
 * Generates long values within [range].
 */
public infix fun RuleTarget<Long>.generatesIn(range: LongRange): GenerationSpec<Long> =
    generatesBy {
        require(!range.isEmpty()) { "range must not be empty." }
        range.random(random)
    }

/**
 * Generates one value from [values].
 */
public infix fun <T> RuleTarget<T>.generatesOneOf(values: Iterable<T>): GenerationSpec<T> =
    generatesBy {
        val list = values.toList()
        require(list.isNotEmpty()) { "values must not be empty." }
        list[random.nextInt(list.size)]
    }

/**
 * Planned API for using automatic generation for this rule target.
 *
 * This is not implemented by the current runtime path.
 */
public fun <T> RuleTarget<T>.autoGenerates(): GenerationSpec<T> = throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Planned API for generating each element for this collection rule target by invoking [generator].
 *
 * This is not implemented by the current runtime path.
 */
public infix fun <Element, CollectionType : Collection<Element>> RuleTarget<CollectionType>.generatesEach(
    generator: Generator<Element>,
): CollectionGenerationSpec<Element, CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")
