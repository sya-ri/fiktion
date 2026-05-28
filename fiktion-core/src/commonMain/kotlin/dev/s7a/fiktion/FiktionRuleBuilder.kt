package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Shared rule registration surface exposed by Fiktion builders.
 */
public sealed interface FiktionRuleBuilder {
    /**
     * Targets every generated value of [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified type<T>() overload.", level = DeprecationLevel.ERROR)
    public fun type(type: KType): RuleTarget<*>

    /**
     * Targets generated values of [value] whose owner is [owner], regardless of property name.
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep [value]
     * and the generator value type consistent.
     */
    @Deprecated("Use the reified property<Owner, Value>() overload.", level = DeprecationLevel.ERROR)
    public fun property(
        owner: KType,
        value: KType,
    ): RuleTarget<*>

    /**
     * Targets values generated for [property].
     *
     * Kotlin common code cannot read the owner and value type from a bare [KProperty1]. Use the property reference
     * infix functions or `property<Owner, Value>(name)` when type-safe matching is required.
     */
    @Deprecated("Use property<Owner, Value>(property.name) or the infix KProperty generates API.", level = DeprecationLevel.ERROR)
    public fun <Owner, Value> property(property: KProperty1<Owner, Value>): RuleTarget<Value>

    /**
     * Targets values generated for [path].
     */
    public fun <Root, Value> property(path: PropertyPath<Root, Value>): RuleTarget<Value>

    /**
     * Targets generated values of [value] whose owner is [owner] and property name is [name].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep [value]
     * and the generator value type consistent.
     */
    @Deprecated("Use the reified property<Owner, Value>(name) overload.", level = DeprecationLevel.ERROR)
    public fun property(
        owner: KType,
        name: String,
        value: KType,
    ): RuleTarget<*>

    /**
     * Targets generated values of [value] whose owner is [owner] and property name matches [regex].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep [value]
     * and the generator value type consistent.
     */
    @Deprecated("Use the reified property<Owner, Value>(regex) overload.", level = DeprecationLevel.ERROR)
    public fun property(
        owner: KType,
        regex: Regex,
        value: KType,
    ): RuleTarget<*>

    /**
     * Targets generated values of [value] whose property name is [name], regardless of owner.
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [value] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified name<Value>(name) overload.", level = DeprecationLevel.ERROR)
    public fun name(
        name: String,
        value: KType,
    ): RuleTarget<*>

    /**
     * Targets generated values of [value] whose property name matches [regex], regardless of owner.
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [value] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified name<Value>(regex) overload.", level = DeprecationLevel.ERROR)
    public fun name(
        regex: Regex,
        value: KType,
    ): RuleTarget<*>

    /**
     * Targets generated values whose property name is [name], inferring the value type from the generator.
     */
    public fun name(name: String): RuleNameTarget

    /**
     * Targets generated values whose property name matches [regex], inferring the value type from the generator.
     */
    public fun name(regex: Regex): RuleNameTarget

    /**
     * Generates [value] for this property.
     */
    @Deprecated("Use property<Owner, Value>(property.name) for type-safe global rules.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Value> KProperty1<Owner, Value>.generates(value: Value): GenerationSpec<Value> =
        property(this).generates(value)

    /**
     * Generates this property by invoking [generator].
     */
    @Deprecated("Use property<Owner, Value>(property.name) for type-safe global rules.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Value> KProperty1<Owner, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        property(this).generatesBy(generator)

    /**
     * Generates this property using Fiktion's automatic generation.
     */
    @Deprecated("Use property<Owner, Value>(property.name) generates auto for type-safe global rules.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Value> KProperty1<Owner, Value>.generates(auto: Auto): GenerationSpec<Value> = property(this) generates auto

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Element, CollectionType : Collection<Element>> KProperty1<Owner, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generatesEach(generator)

    /**
     * Generates each entry for this map property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Key, Value, MapType : Map<Key, Value>> KProperty1<Owner, MapType>.generatesEach(
        generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> = property(this).generatesEach(generator)

    /**
     * Generates map keys for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Key, Value, MapType : Map<Key, Value>> KProperty1<Owner, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> = property(this).generatesKeys(generator)

    /**
     * Generates map values for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Owner, Key, Value, MapType : Map<Key, Value>> KProperty1<Owner, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> = property(this).generatesValues(generator)

    /**
     * Generates [value] for this nested property path.
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        property(this).generates(value)

    /**
     * Generates this nested property path by invoking [generator].
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        property(this).generatesBy(generator)

    /**
     * Generates this nested property path using Fiktion's automatic generation.
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generates(auto: Auto): GenerationSpec<Value> = property(this) generates auto

    /**
     * Generates each element for this nested collection property path by invoking [generator].
     */
    public infix fun <Root, Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generatesEach(generator)

    /**
     * Generates each entry for this nested map property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesEach(
        generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> = property(this).generatesEach(generator)

    /**
     * Generates map keys for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> = property(this).generatesKeys(generator)

    /**
     * Generates map values for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> = property(this).generatesValues(generator)
}

/**
 * Targets every generated value of [T].
 */
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified T> FiktionRuleBuilder.type(): RuleTarget<T> = type(typeOf<T>()) as RuleTarget<T>

/**
 * Targets generated values of [Value] whose owner is [Owner], regardless of property name.
 */
@JvmName("propertyOwnedBy")
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Owner, reified Value> FiktionRuleBuilder.property(): RuleTarget<Value> =
    property(owner = typeOf<Owner>(), value = typeOf<Value>()) as RuleTarget<Value>

/**
 * Targets generated values of [Value] whose owner is [Owner] and property name is [name].
 */
@JvmName("propertyOwnedByName")
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Owner, reified Value> FiktionRuleBuilder.property(name: String): RuleTarget<Value> =
    property(owner = typeOf<Owner>(), name = name, value = typeOf<Value>()) as RuleTarget<Value>

/**
 * Targets generated values of [Value] whose owner is [Owner] and property name matches [regex].
 */
@JvmName("propertyOwnedByRegex")
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Owner, reified Value> FiktionRuleBuilder.property(regex: Regex): RuleTarget<Value> =
    property(owner = typeOf<Owner>(), regex = regex, value = typeOf<Value>()) as RuleTarget<Value>

/**
 * Targets generated values of [Value] whose property name is [name], regardless of owner.
 */
@JvmName("nameByValue")
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Value> FiktionRuleBuilder.name(name: String): RuleTarget<Value> =
    name(name = name, value = typeOf<Value>()) as RuleTarget<Value>

/**
 * Targets generated values of [Value] whose property name matches [regex], regardless of owner.
 */
@JvmName("regexNameByValue")
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Value> FiktionRuleBuilder.name(regex: Regex): RuleTarget<Value> =
    name(regex = regex, value = typeOf<Value>()) as RuleTarget<Value>
