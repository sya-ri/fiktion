package dev.s7a.fiktion

import dev.s7a.fiktion.generators.oneOf
import kotlin.jvm.JvmName
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
        requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
        range.random(random)
    }

/**
 * Generates long values within [range].
 */
public infix fun RuleTarget<Long>.generatesIn(range: LongRange): GenerationSpec<Long> =
    generatesBy {
        requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
        range.random(random)
    }

/**
 * Generates one value from [values].
 */
public infix fun <T> RuleTarget<T>.generatesOneOf(values: Iterable<T>): GenerationSpec<T> =
    generatesBy {
        oneOf(values.toList())
    }

/**
 * Uses Fiktion's automatic generation for this rule target.
 */
@Suppress("UNUSED_PARAMETER")
public infix fun <T> RuleTarget<T>.generates(auto: Auto): GenerationSpec<T> = (this as DefaultRuleTarget<T>).generatesAutomatically()

/**
 * Uses Fiktion's automatic generation for each element of this collection rule target.
 */
@JvmName("generatesAutoCollection")
@Suppress("UNUSED_PARAMETER")
public inline infix fun <reified Element, reified CollectionType : Collection<Element>> RuleTarget<CollectionType>.generates(
    auto: Auto,
): CollectionGenerationSpec<Element, CollectionType> = generatesAutoCollection(target = this, elementType = typeOf<Element>())

/**
 * Uses Fiktion's automatic generation for each key and value of this map rule target.
 */
@JvmName("generatesAutoMap")
@Suppress("UNUSED_PARAMETER")
public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> RuleTarget<MapType>.generates(
    auto: Auto,
): MapGenerationSpec<Key, Value, MapType> = generatesAutoMap(target = this, keyType = typeOf<Key>(), valueType = typeOf<Value>())

/**
 * Registers automatic map generation without exposing the internal target implementation to inline code.
 */
@PublishedApi
internal fun <Key, Value, MapType : Map<Key, Value>> generatesAutoMap(
    target: RuleTarget<MapType>,
    keyType: KType?,
    valueType: KType?,
): MapGenerationSpec<Key, Value, MapType> =
    target.mapGenerationSpec<Key, Value, MapType>().also { spec ->
        spec.keyType = keyType
        spec.valueType = valueType
    }

/**
 * Registers automatic collection generation without exposing the internal target implementation to inline code.
 */
@PublishedApi
internal fun <Element, CollectionType : Collection<Element>> generatesAutoCollection(
    target: RuleTarget<CollectionType>,
    elementType: KType,
): CollectionGenerationSpec<Element, CollectionType> =
    (target as DefaultRuleTarget<CollectionType>).generatesAutomaticCollection(elementType = elementType)

/**
 * Generates each element for this collection rule target by invoking [generator].
 */
public infix fun <Element, CollectionType : Collection<Element>> RuleTarget<CollectionType>.generatesEach(
    generator: Generator<Element>,
): CollectionGenerationSpec<Element, CollectionType> = (this as DefaultRuleTarget<CollectionType>).collectionGenerationSpec(generator)

/**
 * Generates each entry for this map rule target by invoking [generator].
 */
public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> RuleTarget<MapType>.generatesEach(
    noinline generator: Generator<Pair<Key, Value>>,
): MapEntrySpec<Key, Value, MapType> =
    generatesMapEntries(target = this, keyType = typeOf<Key>(), valueType = typeOf<Value>(), generator = generator)

/**
 * Registers entry generation without exposing the internal target implementation to inline code.
 */
@PublishedApi
internal fun <Key, Value, MapType : Map<Key, Value>> generatesMapEntries(
    target: RuleTarget<MapType>,
    keyType: KType?,
    valueType: KType?,
    generator: Generator<Pair<Key, Value>>,
): MapEntrySpec<Key, Value, MapType> {
    val spec = target.mapGenerationSpec<Key, Value, MapType>()
    spec.keyType = keyType
    spec.valueType = valueType
    spec.ensureNoMapParts()
    spec.entryGenerator = generator
    return spec
}

/**
 * Generates map keys for this rule target by invoking [generator].
 */
public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> RuleTarget<MapType>.generatesKeys(
    noinline generator: Generator<Key>,
): MapKeySpec<Key, Value, MapType> =
    generatesMapKeys(target = this, keyType = typeOf<Key>(), valueType = typeOf<Value>(), generator = generator)

/**
 * Registers key generation without exposing the internal target implementation to inline code.
 */
@PublishedApi
internal fun <Key, Value, MapType : Map<Key, Value>> generatesMapKeys(
    target: RuleTarget<MapType>,
    keyType: KType?,
    valueType: KType?,
    generator: Generator<Key>,
): MapKeySpec<Key, Value, MapType> {
    val spec = target.mapGenerationSpec<Key, Value, MapType>()
    spec.keyType = keyType
    spec.valueType = valueType
    spec.ensureNoEntryGenerator()
    requireFiktionConfiguration(spec.keyGenerator == null) { "Map keys are already configured for this rule target." }
    spec.keyGenerator = generator
    return spec
}

/**
 * Generates map values for this rule target by invoking [generator].
 */
public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> RuleTarget<MapType>.generatesValues(
    noinline generator: Generator<Value>,
): MapValueSpec<Key, Value, MapType> =
    generatesMapValues(target = this, keyType = typeOf<Key>(), valueType = typeOf<Value>(), generator = generator)

/**
 * Registers value generation without exposing the internal target implementation to inline code.
 */
@PublishedApi
internal fun <Key, Value, MapType : Map<Key, Value>> generatesMapValues(
    target: RuleTarget<MapType>,
    keyType: KType?,
    valueType: KType?,
    generator: Generator<Value>,
): MapValueSpec<Key, Value, MapType> {
    val spec = target.mapGenerationSpec<Key, Value, MapType>()
    spec.keyType = keyType
    spec.valueType = valueType
    spec.ensureNoEntryGenerator()
    requireFiktionConfiguration(spec.valueGenerator == null) { "Map values are already configured for this rule target." }
    spec.valueGenerator = generator
    return spec
}

/**
 * Returns the map spec attached to this target.
 */
private fun <
    Key,
    Value,
    MapType : Map<Key, Value>,
> RuleTarget<MapType>.mapGenerationSpec(): DefaultMapGenerationSpec<Key, Value, MapType> =
    (this as DefaultRuleTarget<MapType>).mapGenerationSpec()

/**
 * Fails when entry generation has already been configured.
 */
private fun <Key, Value, MapType : Map<Key, Value>> DefaultMapGenerationSpec<Key, Value, MapType>.ensureNoEntryGenerator() {
    requireFiktionConfiguration(entryGenerator == null) { "Map entries are already configured for this rule target." }
}

/**
 * Fails when any map entry part has already been configured.
 */
private fun <Key, Value, MapType : Map<Key, Value>> DefaultMapGenerationSpec<Key, Value, MapType>.ensureNoMapParts() {
    requireFiktionConfiguration(entryGenerator == null) { "Map entries are already configured for this rule target." }
    requireFiktionConfiguration(keyGenerator == null) { "Map keys are already configured for this rule target." }
    requireFiktionConfiguration(valueGenerator == null) { "Map values are already configured for this rule target." }
}
