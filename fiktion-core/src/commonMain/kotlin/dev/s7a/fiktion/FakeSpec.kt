@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.jvm.JvmName
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Per-call configuration for generating a [Root] value.
 */
public class FakeSpec<Root> {
    /**
     * Seed override for this generation call.
     */
    internal var seed: Long? = null

    /**
     * Mutable per-call rule configuration.
     */
    private val config: MutableFiktionConfig = MutableFiktionConfig()

    /**
     * Explicit rules registered in this per-call scope.
     */
    internal val rules: List<DefaultGenerationSpec<*>>
        get() = config.build().rules

    /**
     * Sets the seed for this generation call.
     */
    public infix fun withSeed(seed: Long) {
        this.seed = seed
    }

    /**
     * Targets [property] on [Root].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified Value> property(property: KProperty1<Root, Value>): RuleTarget<Value> =
        property(property = property, value = typeOf<Value>())

    /**
     * Targets [property] on [Root].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [value] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified property(property) overload.", level = DeprecationLevel.ERROR)
    public fun <Value> property(
        property: KProperty1<Root, Value>,
        value: KType,
    ): RuleTarget<Value> = target(listOf(PathRuleSegment(ownerId = null, name = property.name, valueId = value.nonNullTypeId())))

    /**
     * Targets a nested [path] starting from [Root].
     */
    public fun <Value> property(path: PropertyPath<Root, Value>): RuleTarget<Value> = target(path.segments)

    /**
     * Targets properties generated while building [Root] whose value type is [value] and name is [name].
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
     * Targets properties generated while building [Root] whose name is [name], inferring the value type from the generator.
     */
    public fun name(name: String): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.Name(name, type) },
            matcher = { type -> RuleMatcher.Name(name, type) },
            register = config::add,
        )

    /**
     * Targets properties generated while building [Root] whose value type is [value] and name matches [regex].
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
     * Targets properties generated while building [Root] whose name matches [regex], inferring the value type from the generator.
     */
    public fun name(regex: Regex): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.RegexName(regex.pattern, regex.options, type) },
            matcher = { type -> RuleMatcher.RegexName(regex, type) },
            register = config::add,
        )

    /**
     * Targets properties generated while building the current root whose value type is [Value] and name is [name].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        name: String,
        // Keeps this overload distinct from name(name: String), which returns RuleNameTarget.
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(name, typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets properties generated while building the current root whose value type is [Value] and name matches [regex].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        regex: Regex,
        // Keeps this overload distinct from name(regex: Regex), which returns RuleNameTarget.
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(regex, typeOf<Value>()) as RuleTarget<Value>

    /**
     * Generates [value] for this property.
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Value> KProperty1<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        property(property = this, value = typeOf<Value>()).generates(value)

    /**
     * Generates this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Value> KProperty1<Root, Value>.generatesBy(
        noinline generator: Generator<Value>,
    ): GenerationSpec<Value> = property(property = this, value = typeOf<Value>()).generatesBy(generator)

    /**
     * Generates this property using Fiktion's automatic generation.
     */
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public inline infix fun <reified Value> KProperty1<Root, Value>.generates(auto: Auto): GenerationSpec<Value> =
        property(property = this, value = typeOf<Value>()).generates(auto)

    /**
     * Generates this collection property by automatically generating each element.
     */
    @JvmName("generatesAutoCollectionProperty")
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public inline infix fun <reified Element, reified CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generates(
        auto: Auto,
    ): CollectionGenerationSpec<Element, CollectionType> =
        generates(auto = auto, collectionType = typeOf<CollectionType>(), elementType = typeOf<Element>())

    /**
     * Generates this collection property by automatically generating each element.
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
     * and the generator types consistent.
     */
    @Deprecated("Use the reified collection property generates(auto) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public fun <Element, CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generates(
        auto: Auto,
        collectionType: KType,
        elementType: KType,
    ): CollectionGenerationSpec<Element, CollectionType> =
        property(property = this, value = collectionType).generates(auto = auto, elementType = elementType)

    /**
     * Generates this map property by automatically generating each key and value.
     */
    @JvmName("generatesAutoMapProperty")
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generates(
        auto: Auto,
    ): MapGenerationSpec<Key, Value, MapType> =
        generates(auto = auto, mapType = typeOf<MapType>(), keyType = typeOf<Key>(), valueType = typeOf<Value>())

    /**
     * Generates this map property by automatically generating each key and value.
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
     * and the generator types consistent.
     */
    @Deprecated("Use the reified map property generates(auto) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generates(
        auto: Auto,
        mapType: KType,
        keyType: KType,
        valueType: KType,
    ): MapGenerationSpec<Key, Value, MapType> =
        property(property = this, value = mapType).generates(auto = auto, keyType = keyType, valueType = valueType)

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Element, reified CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generatesEach(
        noinline generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = generatesEach(generator = generator, collectionType = typeOf<CollectionType>())

    /**
     * Generates each element for this collection property by invoking [generator].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep
     * [collectionType] and the generator collection type consistent.
     */
    @Deprecated("Use the reified collection property generatesEach(generator) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public fun <Element, CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
        collectionType: KType,
    ): CollectionGenerationSpec<Element, CollectionType> = property(property = this, value = collectionType).generatesEach(generator)

    /**
     * Generates each entry for this map property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesEach(
        noinline generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> =
        generatesEach(generator = generator, mapType = typeOf<MapType>(), keyType = typeOf<Key>(), valueType = typeOf<Value>())

    /**
     * Generates each entry for this map property by invoking [generator].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
     * and the generator types consistent.
     */
    @Deprecated("Use the reified map property generatesEach(generator) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesEach(
        generator: Generator<Pair<Key, Value>>,
        mapType: KType,
        keyType: KType,
        valueType: KType,
    ): MapEntrySpec<Key, Value, MapType> =
        property(property = this, value = mapType).generatesEach(generator = generator, keyType = keyType, valueType = valueType)

    /**
     * Generates map keys for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesKeys(
        noinline generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> =
        generatesKeys(generator = generator, mapType = typeOf<MapType>(), keyType = typeOf<Key>(), valueType = typeOf<Value>())

    /**
     * Generates map keys for this property by invoking [generator].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
     * and the generator types consistent.
     */
    @Deprecated("Use the reified property generatesKeys(generator) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
        mapType: KType,
        keyType: KType,
        valueType: KType,
    ): MapKeySpec<Key, Value, MapType> =
        property(property = this, value = mapType).generatesKeys(generator = generator, keyType = keyType, valueType = valueType)

    /**
     * Generates map values for this property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesValues(
        noinline generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        generatesValues(generator = generator, mapType = typeOf<MapType>(), keyType = typeOf<Key>(), valueType = typeOf<Value>())

    /**
     * Generates map values for this property by invoking [generator].
     *
     * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
     * and the generator types consistent.
     */
    @Deprecated("Use the reified property generatesValues(generator) overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesValues(
        generator: Generator<Value>,
        mapType: KType,
        keyType: KType,
        valueType: KType,
    ): MapValueSpec<Key, Value, MapType> =
        property(property = this, value = mapType).generatesValues(generator = generator, keyType = keyType, valueType = valueType)

    /**
     * Applies nested per-call configuration to this property.
     */
    @Suppress("DEPRECATION_ERROR")
    public inline operator fun <reified Value> KProperty1<Root, Value>.invoke(
        noinline configure: FakeSpec<Value>.() -> Unit,
    ): GenerationSpec<Value> = invoke(configure = configure, value = typeOf<Value>())

    /**
     * Applies nested per-call configuration to this property.
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [value] and
     * the generator value type consistent.
     */
    @Deprecated("Use the reified property invoke overload.", level = DeprecationLevel.ERROR)
    @Suppress("DEPRECATION_ERROR")
    public operator fun <Value> KProperty1<Root, Value>.invoke(
        configure: FakeSpec<Value>.() -> Unit,
        value: KType,
    ): GenerationSpec<Value> {
        configureNestedRules(
            prefix =
                listOf(
                    PathRuleSegment(
                        ownerId = null,
                        name = name,
                        valueId = value.nonNullTypeId(),
                    ),
                ),
            configure = configure,
        )
        return property(property = this, value = value) generates auto
    }

    /**
     * Generates [value] for this nested property path.
     */
    public infix fun <Value> PropertyPath<Root, Value>.generates(value: Value): GenerationSpec<Value> = property(this).generates(value)

    /**
     * Generates this nested property path by invoking [generator].
     */
    public infix fun <Value> PropertyPath<Root, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        property(this).generatesBy(generator)

    /**
     * Generates this nested property path using Fiktion's automatic generation.
     */
    public infix fun <Value> PropertyPath<Root, Value>.generates(auto: Auto): GenerationSpec<Value> = property(this) generates auto

    /**
     * Generates this nested collection property path by automatically generating each element.
     */
    @JvmName("generatesAutoCollectionPath")
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generates(
        auto: Auto,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generates(auto = auto, elementType = collectionElementType())

    /**
     * Generates this nested map property path by automatically generating each key and value.
     */
    @JvmName("generatesAutoMapPath")
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generates(
        auto: Auto,
    ): MapGenerationSpec<Key, Value, MapType> = property(this).generates(auto = auto, keyType = mapKeyType(), valueType = mapValueType())

    /**
     * Generates each element for this nested collection property path by invoking [generator].
     */
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generatesEach(generator)

    /**
     * Generates each entry for this nested map property path by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesEach(
        generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> =
        property(this).generatesEach(
            generator = generator,
            keyType = mapKeyType(),
            valueType = mapValueType(),
        )

    /**
     * Generates map keys for this nested property path by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> =
        property(this).generatesKeys(
            generator = generator,
            keyType = mapKeyType(),
            valueType = mapValueType(),
        )

    /**
     * Generates map values for this nested property path by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        property(this).generatesValues(
            generator = generator,
            keyType = mapKeyType(),
            valueType = mapValueType(),
        )

    /**
     * Applies nested per-call configuration to this property path.
     */
    public operator fun <Value> PropertyPath<Root, Value>.invoke(configure: FakeSpec<Value>.() -> Unit): GenerationSpec<Value> {
        configureNestedRules(prefix = segments, configure = configure)
        return property(this) generates auto
    }

    /**
     * Targets a property path represented as raw Kotlin properties.
     */
    private fun <Value> target(segments: List<PathRuleSegment>): RuleTarget<Value> =
        target(RuleKey.Path(segments), RuleMatcher.Path(segments))

    /**
     * Registers rules from [configure] below a nested path [prefix].
     */
    private fun <Value> configureNestedRules(
        prefix: List<PathRuleSegment>,
        configure: FakeSpec<Value>.() -> Unit,
    ) {
        val nestedSpec = FakeSpec<Value>()
        nestedSpec.configure()
        nestedSpec.rules.forEach { rule ->
            config.add(rule.prefixedBy(prefix))
        }
    }

    /**
     * Registers a rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> target(
        key: RuleKey,
        matcher: RuleMatcher,
    ): RuleTarget<Value> =
        DefaultRuleTarget(
            config = config,
            key = key,
            matcher = matcher,
        )
}
