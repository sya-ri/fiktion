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
 * Generates each element for this collection rule target by invoking [generator].
 */
public infix fun <Element, CollectionType : Collection<Element>> RuleTarget<CollectionType>.generatesEach(
    generator: Generator<Element>,
): CollectionGenerationSpec<Element, CollectionType> = (this as DefaultRuleTarget<CollectionType>).collectionGenerationSpec(generator)

/**
 * Generates each entry for this map rule target by invoking [generator].
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.generatesEach(
    generator: Generator<Pair<Key, Value>>,
): MapEntrySpec<Key, Value, MapType> {
    val spec = mapGenerationSpec<Key, Value, MapType>()
    spec.ensureNoMapParts()
    spec.entryGenerator = generator
    return spec
}

/**
 * Generates map keys for this rule target by invoking [generator].
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.generatesKeys(
    generator: Generator<Key>,
): MapKeySpec<Key, Value, MapType> {
    val spec = mapGenerationSpec<Key, Value, MapType>()
    spec.ensureNoEntryGenerator()
    require(spec.keyGenerator == null) { "Map keys are already configured for this rule target." }
    spec.keyGenerator = generator
    return spec
}

/**
 * Generates map values for this rule target by invoking [generator].
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.generatesValues(
    generator: Generator<Value>,
): MapValueSpec<Key, Value, MapType> {
    val spec = mapGenerationSpec<Key, Value, MapType>()
    spec.ensureNoEntryGenerator()
    require(spec.valueGenerator == null) { "Map values are already configured for this rule target." }
    spec.valueGenerator = generator
    return spec
}

/**
 * Returns the map spec attached to this target.
 */
private fun <Key, Value, MapType : Map<Key, Value>> RuleTarget<MapType>.mapGenerationSpec(): DefaultMapGenerationSpec<Key, Value, MapType> =
    (this as DefaultRuleTarget<MapType>).mapGenerationSpec()

/**
 * Fails when entry generation has already been configured.
 */
private fun <Key, Value, MapType : Map<Key, Value>> DefaultMapGenerationSpec<Key, Value, MapType>.ensureNoEntryGenerator() {
    require(entryGenerator == null) { "Map entries are already configured for this rule target." }
}

/**
 * Fails when any map entry part has already been configured.
 */
private fun <Key, Value, MapType : Map<Key, Value>> DefaultMapGenerationSpec<Key, Value, MapType>.ensureNoMapParts() {
    require(entryGenerator == null) { "Map entries are already configured for this rule target." }
    require(keyGenerator == null) { "Map keys are already configured for this rule target." }
    require(valueGenerator == null) { "Map values are already configured for this rule target." }
}
