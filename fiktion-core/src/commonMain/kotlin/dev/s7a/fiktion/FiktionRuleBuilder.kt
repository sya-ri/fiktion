package dev.s7a.fiktion

import kotlin.jvm.JvmName
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Shared rule registration surface exposed by Fiktion builders.
 */
public sealed class FiktionRuleBuilder protected constructor() {
    /**
     * Mutable configuration receiving rules registered through this builder.
     */
    internal abstract val config: MutableFiktionConfig

    /**
     * Targets every generated value of [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified type<T>() overload.", level = DeprecationLevel.ERROR)
    public fun type(type: KType): RuleTarget<*> = target<Any?>(RuleKey.Type(type), RuleMatcher.Type(type))

    /**
     * Targets every generated value whose type belongs to [type]'s type family.
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified typeFamily<T>() overload.", level = DeprecationLevel.ERROR)
    public fun typeFamily(type: KType): TypeFamilyRuleTarget<*> =
        typeFamilyTarget<Any?>(RuleKey.TypeFamily(type), RuleMatcher.TypeFamily(type))

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
    ): RuleTarget<*> = target<Any?>(RuleKey.OwnedType(owner, value), RuleMatcher.OwnedType(owner, value))

    /**
     * Targets values generated for [property].
     *
     * Kotlin common code cannot read the owner and value type from a bare [KProperty1]. Use the property reference
     * infix functions or `property<Owner, Value>(name)` when type-safe matching is required.
     */
    @Deprecated("Use property<Owner, Value>(property.name) or the infix KProperty generates API.", level = DeprecationLevel.ERROR)
    public fun <Owner, Value> property(property: KProperty1<Owner, Value>): RuleTarget<Value> =
        target(listOf(PathRuleSegment(ownerId = null, name = property.name, valueId = null)))

    /**
     * Targets values generated for [path].
     */
    public fun <Root, Value> property(path: PropertyPath<Root, Value>): RuleTarget<Value> = target(path.segments)

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
    ): RuleTarget<*> = target<Any?>(RuleKey.Property(owner, name, value), RuleMatcher.Property(owner, name, value))

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
    ): RuleTarget<*> =
        target<Any?>(
            RuleKey.OwnedRegexName(owner, regex.pattern, regex.options, value),
            RuleMatcher.OwnedRegexName(owner, regex, value),
        )

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
    ): RuleTarget<*> = target<Any?>(RuleKey.Name(name, value), RuleMatcher.Name(name, value))

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
    ): RuleTarget<*> = target<Any?>(RuleKey.RegexName(regex.pattern, regex.options, value), RuleMatcher.RegexName(regex, value))

    /**
     * Targets generated values whose property name is [name], inferring the value type from the generator.
     */
    public fun name(name: String): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.Name(name, type) },
            matcher = { type -> RuleMatcher.Name(name, type) },
            register = config::add,
        )

    /**
     * Targets generated values whose property name matches [regex], inferring the value type from the generator.
     */
    public fun name(regex: Regex): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.RegexName(regex.pattern, regex.options, type) },
            matcher = { type -> RuleMatcher.RegexName(regex, type) },
            register = config::add,
        )

    /**
     * Generates [value] for this property.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generates(value: Value): GenerationSpec<Value> =
        (property(owner = typeOf<Owner>(), name = name, value = typeOf<Value>()) as RuleTarget<Value>)
            .generates(value)

    /**
     * Generates this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generatesBy(
        noinline generator: Generator<Value>,
    ): GenerationSpec<Value> =
        (property(owner = typeOf<Owner>(), name = name, value = typeOf<Value>()) as RuleTarget<Value>)
            .generatesBy(generator)

    /**
     * Generates this property using Fiktion's automatic generation.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generates(auto: Auto): GenerationSpec<Value> =
        (property(owner = typeOf<Owner>(), name = name, value = typeOf<Value>()) as RuleTarget<Value>) generates auto

    /**
     * Generates this collection property by automatically generating each element.
     */
    @JvmName("generatesAutoCollectionProperty")
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline infix fun <
        reified Owner,
        reified Element,
        reified CollectionType : Collection<Element>,
    > KProperty1<Owner, CollectionType>.generates(
        auto: Auto,
    ): CollectionGenerationSpec<Element, CollectionType> =
        generatesAutoCollection(
            target =
                property(
                    owner = typeOf<Owner>(),
                    name = name,
                    value = typeOf<CollectionType>(),
                ) as RuleTarget<CollectionType>,
            elementType = typeOf<Element>(),
        )

    /**
     * Generates this map property by automatically generating each key and value.
     */
    @JvmName("generatesAutoMapProperty")
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline infix fun <
        reified Owner,
        reified Key,
        reified Value,
        reified MapType : Map<Key, Value>,
    > KProperty1<Owner, MapType>.generates(
        auto: Auto,
    ): MapGenerationSpec<Key, Value, MapType> =
        generatesAutoMap(
            target =
                property(
                    owner = typeOf<Owner>(),
                    name = name,
                    value = typeOf<MapType>(),
                ) as RuleTarget<MapType>,
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
        )

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <
        reified Owner,
        reified Element,
        reified CollectionType : Collection<Element>,
    > KProperty1<Owner, CollectionType>.generatesEach(
        noinline generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> =
        (
            property(
                owner = typeOf<Owner>(),
                name = name,
                value = typeOf<CollectionType>(),
            ) as RuleTarget<CollectionType>
        ).generatesEach(generator)

    /**
     * Generates each entry for this map property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <
        reified Owner,
        reified Key,
        reified Value,
        reified MapType : Map<Key, Value>,
    > KProperty1<Owner, MapType>.generatesEach(
        noinline generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> =
        generatesMapEntries(
            target =
                property(
                    owner = typeOf<Owner>(),
                    name = name,
                    value = typeOf<MapType>(),
                ) as RuleTarget<MapType>,
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

    /**
     * Generates map keys for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <
        reified Owner,
        reified Key,
        reified Value,
        reified MapType : Map<Key, Value>,
    > KProperty1<Owner, MapType>.generatesKeys(
        noinline generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> =
        generatesMapKeys(
            target =
                property(
                    owner = typeOf<Owner>(),
                    name = name,
                    value = typeOf<MapType>(),
                ) as RuleTarget<MapType>,
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

    /**
     * Generates map values for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline infix fun <
        reified Owner,
        reified Key,
        reified Value,
        reified MapType : Map<Key, Value>,
    > KProperty1<Owner, MapType>.generatesValues(
        noinline generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        generatesMapValues(
            target =
                property(
                    owner = typeOf<Owner>(),
                    name = name,
                    value = typeOf<MapType>(),
                ) as RuleTarget<MapType>,
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

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
    ): MapEntrySpec<Key, Value, MapType> =
        generatesMapEntries(
            target = property(this),
            keyType = mapKeyType(),
            valueType = mapValueType(),
            generator = generator,
        )

    /**
     * Generates map keys for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> =
        generatesMapKeys(
            target = property(this),
            keyType = mapKeyType(),
            valueType = mapValueType(),
            generator = generator,
        )

    /**
     * Generates map values for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        generatesMapValues(
            target = property(this),
            keyType = mapKeyType(),
            valueType = mapValueType(),
            generator = generator,
        )

    /**
     * Targets every generated value of [T].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified T> type(): RuleTarget<T> = type(typeOf<T>()) as RuleTarget<T>

    /**
     * Targets every generated value whose type belongs to [T]'s type family.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified T> typeFamily(): TypeFamilyRuleTarget<T> = typeFamily(typeOf<T>()) as TypeFamilyRuleTarget<T>

    /**
     * Configures how generated elements are materialized as collection type [CollectionType].
     */
    public inline fun <reified CollectionType : Collection<*>> configureCollection(noinline convert: (List<Any?>) -> CollectionType) {
        configureCollection(type = typeOf<CollectionType>(), convert = convert)
    }

    /**
     * Configures how generated entries are materialized as map type [MapType].
     */
    public inline fun <reified MapType : Map<*, *>> configureMap(noinline convert: (List<Pair<Any?, Any?>>) -> MapType) {
        configureMap(type = typeOf<MapType>(), convert = convert)
    }

    /**
     * Registers a collection converter without exposing internal config to inline code.
     */
    @PublishedApi
    internal fun configureCollection(
        type: KType,
        convert: (List<Any?>) -> Collection<*>,
    ) {
        config.add(CollectionConverter(classifier = type.classifier, convert = convert))
    }

    /**
     * Registers a map converter without exposing internal config to inline code.
     */
    @PublishedApi
    internal fun configureMap(
        type: KType,
        convert: (List<Pair<Any?, Any?>>) -> Map<*, *>,
    ) {
        config.add(MapConverter(classifier = type.classifier, convert = convert))
    }

    /**
     * Targets generated values of [Value] whose owner is [Owner], regardless of property name.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified Owner, reified Value> property(): RuleTarget<Value> =
        property(owner = typeOf<Owner>(), value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets generated values of [Value] whose owner is [Owner] and property name is [name].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified Owner, reified Value> property(name: String): RuleTarget<Value> =
        property(owner = typeOf<Owner>(), name = name, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets generated values of [Value] whose owner is [Owner] and property name matches [regex].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified Owner, reified Value> property(regex: Regex): RuleTarget<Value> =
        property(owner = typeOf<Owner>(), regex = regex, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets generated values of [Value] whose property name is [name], regardless of owner.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        name: String,
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(name = name, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets generated values of [Value] whose property name matches [regex], regardless of owner.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        regex: Regex,
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(regex = regex, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets a nested property path represented as raw path segments.
     */
    private fun <Value> target(segments: List<PathRuleSegment>): RuleTarget<Value> =
        target(RuleKey.Path(segments), RuleMatcher.Path(segments))

    /**
     * Creates a rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> target(
        key: RuleKey,
        matcher: RuleMatcher,
    ): RuleTarget<Value> = DefaultRuleTarget(config, key, matcher)

    /**
     * Creates a type-family rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> typeFamilyTarget(
        key: RuleKey,
        matcher: RuleMatcher,
    ): TypeFamilyRuleTarget<Value> = DefaultTypeFamilyRuleTarget(config, key, matcher)
}
