@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator
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
    internal val rules: List<RegisteredRule<*>>
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
     * Planned API for generating this property using automatic generation.
     *
     * This is not implemented by the current runtime path.
     */
    public fun <Value> KProperty1<Root, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Automatic property generation is not implemented yet.")

    /**
     * Planned API for generating each element for this collection property by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Element, CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Planned API for generating map keys for this property by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Planned API for generating map values for this property by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Planned API for generating this property by applying nested per-call configuration to [Value].
     *
     * This is not implemented by the current runtime path.
     */
    public inline operator fun <reified Value> KProperty1<Root, Value>.invoke(
        noinline configure: FakeSpec<Value>.() -> Unit,
    ): GenerationSpec<Value> = throw NotImplementedError("Nested rule configuration is not implemented yet.")

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
     * Planned API for generating this nested property path using automatic generation.
     *
     * This is not implemented by the current runtime path.
     */
    public fun <Value> PropertyPath<Root, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Automatic property generation is not implemented yet.")

    /**
     * Planned API for generating each element for this nested collection property path by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): CollectionGenerationSpec<Element, CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Planned API for generating map keys for this nested property path by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Planned API for generating map values for this nested property path by invoking [generator].
     *
     * This is not implemented by the current runtime path.
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Planned API for generating this nested property path by applying nested per-call configuration to [Value].
     *
     * This is not implemented by the current runtime path.
     */
    public inline operator fun <reified Value> PropertyPath<Root, Value>.invoke(
        noinline configure: FakeSpec<Value>.() -> Unit,
    ): GenerationSpec<Value> = throw NotImplementedError("Nested rule configuration is not implemented yet.")

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
     * Registers a rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> target(
        key: RuleKey,
        matcher: RuleMatcher,
    ): RuleTarget<Value> =
        DefaultRuleTarget { generator ->
            val rule = RegisteredRule(key = key, matcher = matcher, generator = generator)
            config.add(rule)
            rule
        }
}

/**
 * Targets properties generated while building the current root whose value type is [Value] and name is [name].
 */
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Value> FakeSpec<*>.name(name: String): RuleTarget<Value> = name(name, typeOf<Value>()) as RuleTarget<Value>

/**
 * Targets properties generated while building the current root whose value type is [Value] and name matches [regex].
 */
@Suppress("DEPRECATION_ERROR", "UNCHECKED_CAST")
public inline fun <reified Value> FakeSpec<*>.name(regex: Regex): RuleTarget<Value> = name(regex, typeOf<Value>()) as RuleTarget<Value>
