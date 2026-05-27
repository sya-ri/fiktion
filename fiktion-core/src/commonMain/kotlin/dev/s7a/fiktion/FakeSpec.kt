package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator
import kotlin.reflect.KProperty1

/**
 * Per-call configuration for generating a [Root] value.
 */
public class FakeSpec<Root> {
    /**
     * Sets the seed for this generation call.
     */
    public infix fun withSeed(seed: Long): Unit = throw NotImplementedError("Seed configuration is not implemented yet.")

    /**
     * Targets [property] on [Root].
     */
    public fun <Value> rule(property: KProperty1<Root, Value>): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets a nested [path] starting from [Root].
     */
    public fun <Value> rule(path: PropertyPath<Root, Value>): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets properties generated while building [Root] whose value type is [Value] and name is [name].
     */
    public inline fun <reified Value> rule(name: String): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets properties generated while building [Root] whose value type is [Value] and name matches [regex].
     */
    public inline fun <reified Value> rule(regex: Regex): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for properties generated while building [Root] whose name is this string.
     */
    public infix fun <Value> String.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates properties while building [Root] whose name is this string by invoking [generator].
     */
    public infix fun <Value> String.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for properties generated while building [Root] whose name matches this regex.
     */
    public infix fun <Value> Regex.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates properties while building [Root] whose name matches this regex by invoking [generator].
     */
    public infix fun <Value> Regex.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for this property.
     */
    public infix fun <Value> KProperty1<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this property by invoking [generator].
     */
    public infix fun <Value> KProperty1<Root, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this property using automatic generation.
     */
    public fun <Value> KProperty1<Root, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    public infix fun <Element, CollectionType : Collection<Element>> KProperty1<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Generates map keys for this property by invoking [generator].
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates map values for this property by invoking [generator].
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> KProperty1<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates this property by applying nested per-call configuration to [Value].
     */
    public inline operator fun <reified Value> KProperty1<Root, Value>.invoke(
        noinline configure: FakeSpec<Value>.() -> Unit,
    ): GenerationSpec<Value> = throw NotImplementedError("Nested rule configuration is not implemented yet.")

    /**
     * Generates [value] for this nested property path.
     */
    public infix fun <Value> PropertyPath<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this nested property path by invoking [generator].
     */
    public infix fun <Value> PropertyPath<Root, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this nested property path using automatic generation.
     */
    public fun <Value> PropertyPath<Root, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates each element for this nested collection property path by invoking [generator].
     */
    public infix fun <Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Generates map keys for this nested property path by invoking [generator].
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates map values for this nested property path by invoking [generator].
     */
    public infix fun <Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates this nested property path by applying nested per-call configuration to [Value].
     */
    public inline operator fun <reified Value> PropertyPath<Root, Value>.invoke(
        noinline configure: FakeSpec<Value>.() -> Unit,
    ): GenerationSpec<Value> = throw NotImplementedError("Nested rule configuration is not implemented yet.")
}
