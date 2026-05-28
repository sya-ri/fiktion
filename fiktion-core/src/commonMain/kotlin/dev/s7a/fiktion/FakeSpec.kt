@file:OptIn(dev.s7a.fiktion.ExperimentalFiktionApi::class)

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
     *
     * Kotlin common code cannot read the value type from a bare [KProperty1]. Use the property reference infix
     * functions when type-safe matching is required.
     */
    @Deprecated("Use the infix KProperty generates API.", level = DeprecationLevel.ERROR)
    public fun <Value> property(property: KProperty1<Root, Value>): RuleTarget<Value> =
        target(listOf(PathRuleSegment(ownerId = null, name = property.name, valueId = null)))

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
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(name, typeOf<Value>()) as RuleTarget<Value>

    /**
     * Targets properties generated while building the current root whose value type is [Value] and name matches [regex].
     */
    @Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST", "UNUSED_PARAMETER")
    public inline fun <reified Value> name(
        regex: Regex,
        typed: Unit = Unit,
    ): RuleTarget<Value> = name(regex, typeOf<Value>()) as RuleTarget<Value>

    /**
     * Generates [value] for this property.
     */
    public inline infix fun <reified Value> KProperty1<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        propertyByValueType<Value>(
            name = name,
            valueId = typeOf<Value>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
        ).generates(value)

    /**
     * Generates this property by invoking [generator].
     */
    public inline infix fun <reified Value> KProperty1<Root, Value>.generatesBy(
        noinline generator: Generator<Value>,
    ): GenerationSpec<Value> =
        propertyByValueType<Value>(
            name = name,
            valueId = typeOf<Value>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
        ).generatesBy(generator)

    /**
     * Generates this property using Fiktion's automatic generation.
     */
    @Suppress("UNUSED_PARAMETER")
    public inline infix fun <reified Value> KProperty1<Root, Value>.generates(auto: Auto): GenerationSpec<Value> =
        propertyByValueType<Value>(
            name = name,
            valueId = typeOf<Value>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
        ).generates(auto)

    /**
     * Generates this collection property by automatically generating each element.
     */
    @JvmName("generatesAutoCollectionProperty")
    @Suppress("DEPRECATION_ERROR")
    public inline infix fun <reified Element, reified CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generates(
        auto: Auto,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this) generates auto

    /**
     * Generates this map property by automatically generating each key and value.
     */
    @JvmName("generatesAutoMapProperty")
    @Suppress("UNUSED_PARAMETER")
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generates(
        auto: Auto,
    ): MapGenerationSpec<Key, Value, MapType> =
        generatesAutoMap(
            target =
                propertyByValueType<MapType>(
                    name = name,
                    valueId = typeOf<MapType>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
        )

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    @Suppress("DEPRECATION_ERROR")
    public infix fun <Element, CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generatesEach(generator)

    /**
     * Generates each entry for this map property by invoking [generator].
     */
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesEach(
        noinline generator: Generator<Pair<Key, Value>>,
    ): MapEntrySpec<Key, Value, MapType> =
        generatesMapEntries(
            target =
                propertyByValueType<MapType>(
                    name = name,
                    valueId = typeOf<MapType>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

    /**
     * Generates map keys for this property by invoking [generator].
     */
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesKeys(
        noinline generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> =
        generatesMapKeys(
            target =
                propertyByValueType<MapType>(
                    name = name,
                    valueId = typeOf<MapType>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

    /**
     * Generates map values for this property by invoking [generator].
     */
    public inline infix fun <reified Key, reified Value, reified MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesValues(
        noinline generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        generatesMapValues(
            target =
                propertyByValueType<MapType>(
                    name = name,
                    valueId = typeOf<MapType>().toString().removeSuffix(" (Kotlin reflection is not available)").removeSuffix("?"),
                ),
            keyType = typeOf<Key>(),
            valueType = typeOf<Value>(),
            generator = generator,
        )

    /**
     * Applies nested per-call configuration to this property.
     */
    @Suppress("DEPRECATION_ERROR")
    public operator fun <Value> KProperty1<Root, Value>.invoke(configure: FakeSpec<Value>.() -> Unit): GenerationSpec<Value> {
        configureNestedRules(
            prefix =
                listOf(
                    PathRuleSegment(
                        ownerId = null,
                        name = name,
                        valueId = null,
                    ),
                ),
            configure = configure,
        )
        return property(this) generates auto
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
    @Suppress("UNUSED_PARAMETER")
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generates(
        auto: Auto,
    ): CollectionGenerationSpec<Element, CollectionType> =
        generatesAutoCollection(target = property(this), elementType = collectionElementType())

    /**
     * Generates this nested map property path by automatically generating each key and value.
     */
    @JvmName("generatesAutoMapPath")
    @Suppress("UNUSED_PARAMETER")
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generates(
        auto: Auto,
    ): MapGenerationSpec<Key, Value, MapType> =
        generatesAutoMap(target = property(this), keyType = mapKeyType(), valueType = mapValueType())

    /**
     * Generates each element for this nested collection property path by invoking [generator].
     */
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = property(this).generatesEach(generator)

    /**
     * Generates each entry for this nested map property path by invoking [generator].
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesEach(
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
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
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
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> =
        generatesMapValues(
            target = property(this),
            keyType = mapKeyType(),
            valueType = mapValueType(),
            generator = generator,
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
     * Targets a root property when only the property name and value type are known.
     */
    @PublishedApi
    internal fun <Value> propertyByValueType(
        name: String,
        valueId: String?,
    ): RuleTarget<Value> = target(listOf(PathRuleSegment(ownerId = null, name = name, valueId = valueId)))

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
