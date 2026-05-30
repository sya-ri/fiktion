package dev.s7a.fiktion

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
    internal abstract val mutableConfig: MutableFiktionConfig

    /**
     * Configures built-in or add-on generator behavior for every generated value matching [key]'s scope.
     */
    public fun <Scope, Value : Any> using(
        key: FiktionConfig<Scope, Value>,
        value: Value,
    ) {
        using(key(value))
    }

    /**
     * Configures built-in or add-on generator behavior for every generated value matching [value]'s scope.
     */
    public infix fun <Scope, Value : Any> using(value: FiktionConfigSetting<Scope, Value>) {
        mutableConfig.add(DefaultConfigSpec(key = value.key, matcher = RuleMatcher.All, value = value.value))
    }

    /**
     * Targets every generated value of [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified type<T>() overload.", level = DeprecationLevel.ERROR)
    public fun type(type: KType): RuleTarget<*> = target<Any?>(RuleKey.Type(type), RuleMatcher.Type(type), targetType = type)

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
    ): RuleTarget<*> = target<Any?>(RuleKey.OwnedType(owner, value), RuleMatcher.OwnedType(owner, value), targetType = value)

    /**
     * Targets values generated for [property].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified Owner, reified Value> property(property: KProperty1<Owner, Value>): RuleTarget<Value> =
        property(property = property, owner = typeOf<Owner>(), value = typeOf<Value>())

    /**
     * Targets values generated for [path].
     */
    public fun <Root, Value> property(path: PropertyPath<Root, Value>): RuleTarget<Value> =
        target(segments = path.segments, type = path.valueType)

    /**
     * Groups declarations for values generated for [property].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified Owner, reified Value> property(
        property: KProperty1<Owner, Value>,
        noinline configure: RuleTarget<Value>.() -> Unit,
    ): RuleTarget<Value> = property(property).invoke(configure)

    /**
     * Groups declarations for values generated for [path].
     */
    public fun <Root, Value> property(
        path: PropertyPath<Root, Value>,
        configure: RuleTarget<Value>.() -> Unit,
    ): RuleTarget<Value> = property(path).invoke(configure)

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
    ): RuleTarget<*> = target<Any?>(RuleKey.Property(owner, name, value), RuleMatcher.Property(owner, name, value), targetType = value)

    /**
     * Targets generated values for [property] whose owner is [owner] and value type is [value].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep [value]
     * and the generator value type consistent.
     */
    @Deprecated("Use the reified property(property) overload.", level = DeprecationLevel.ERROR)
    public fun <Owner, Value> property(
        property: KProperty1<Owner, Value>,
        owner: KType,
        value: KType,
    ): RuleTarget<Value> =
        target(RuleKey.Property(owner, property.name, value), RuleMatcher.Property(owner, property.name, value), targetType = value)

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
            targetType = value,
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
    ): RuleTarget<*> = target<Any?>(RuleKey.Name(name, value), RuleMatcher.Name(name, value), targetType = value)

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
    ): RuleTarget<*> =
        target<Any?>(RuleKey.RegexName(regex.pattern, regex.options, value), RuleMatcher.RegexName(regex, value), targetType = value)

    /**
     * Targets generated values whose property name is [name], inferring the value type from the generator.
     */
    public fun name(name: String): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.Name(name, type) },
            matcher = { type -> RuleMatcher.Name(name, type) },
            register = mutableConfig::add,
        )

    /**
     * Targets generated values whose property name matches [regex], inferring the value type from the generator.
     */
    public fun name(regex: Regex): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.RegexName(regex.pattern, regex.options, type) },
            matcher = { type -> RuleMatcher.RegexName(regex, type) },
            register = mutableConfig::add,
        )

    /**
     * Generates [value] for this property.
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generates(value: Value): GenerationSpec<Value> =
        property(property = this, owner = typeOf<Owner>(), value = typeOf<Value>()).generates(value)

    /**
     * Generates this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generatesBy(
        noinline generator: Generator<Value>,
    ): GenerationSpec<Value> = property(property = this, owner = typeOf<Owner>(), value = typeOf<Value>()).generatesBy(generator)

    /**
     * Generates this property using Fiktion's automatic generation.
     */
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generates(auto: Auto): GenerationSpec<Value> =
        property(property = this, owner = typeOf<Owner>(), value = typeOf<Value>()) generates auto

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
     * Targets every generated value of [T].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified T> type(): RuleTarget<T> = type(typeOf<T>()) as RuleTarget<T>

    /**
     * Groups declarations for every generated value of [T].
     */
    public inline fun <reified T> type(noinline configure: RuleTarget<T>.() -> Unit): RuleTarget<T> = type<T>().invoke(configure)

    /**
     * Targets every generated value whose type belongs to [T]'s type family.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
    public inline fun <reified T> typeFamily(): TypeFamilyRuleTarget<T> = typeFamily(typeOf<T>()) as TypeFamilyRuleTarget<T>

    /**
     * Configures how generated elements are materialized as collection type [CollectionType].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified CollectionType : Collection<*>> configureCollection(noinline convert: (List<Any?>) -> CollectionType) {
        configureCollection(type = typeOf<CollectionType>(), convert = convert)
    }

    /**
     * Configures how generated entries are materialized as map type [MapType].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified MapType : Map<*, *>> configureMap(noinline convert: (List<Pair<Any?, Any?>>) -> MapType) {
        configureMap(type = typeOf<MapType>(), convert = convert)
    }

    /**
     * Configures how generated elements are materialized as collection type [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the converter type consistent.
     */
    @Deprecated("Use the reified configureCollection<CollectionType>(convert) overload.", level = DeprecationLevel.ERROR)
    public fun configureCollection(
        type: KType,
        convert: (List<Any?>) -> Collection<*>,
    ) {
        mutableConfig.add(CollectionConverter(classifier = type.classifier, convert = convert))
    }

    /**
     * Configures how generated entries are materialized as map type [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the converter type consistent.
     */
    @Deprecated("Use the reified configureMap<MapType>(convert) overload.", level = DeprecationLevel.ERROR)
    public fun configureMap(
        type: KType,
        convert: (List<Pair<Any?, Any?>>) -> Map<*, *>,
    ) {
        mutableConfig.add(MapConverter(classifier = type.classifier, convert = convert))
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
        // Keeps this overload distinct from name(name: String), which returns RuleNameTarget.
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(name = name, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets generated values of [Value] whose property name matches [regex], regardless of owner.
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        regex: Regex,
        // Keeps this overload distinct from name(regex: Regex), which returns RuleNameTarget.
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(regex = regex, value = typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets a nested property path represented as raw path segments.
     */
    private fun <Value> target(
        segments: List<PathRuleSegment>,
        type: KType,
    ): RuleTarget<Value> = target(RuleKey.Path(segments), RuleMatcher.Path(segments), targetType = type)

    /**
     * Creates a rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> target(
        key: RuleKey,
        matcher: RuleMatcher,
        targetType: KType? = null,
    ): RuleTarget<Value> = DefaultRuleTarget(mutableConfig, key, matcher, targetType)

    /**
     * Creates a type-family rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> typeFamilyTarget(
        key: RuleKey,
        matcher: RuleMatcher,
    ): TypeFamilyRuleTarget<Value> = DefaultTypeFamilyRuleTarget(mutableConfig, key, matcher)
}
