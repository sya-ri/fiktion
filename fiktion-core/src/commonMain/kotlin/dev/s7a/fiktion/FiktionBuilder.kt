package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty1

/**
 * Unrestricted builder for global or isolated Fiktion configuration.
 */
public class FiktionBuilder {
    /**
     * Sets the root seed used by generated values.
     */
    public infix fun withSeed(seed: Long): Unit = throw NotImplementedError("Seed configuration is not implemented yet.")

    /**
     * Installs reusable rules from [addon].
     */
    public fun install(addon: FiktionAddon): Unit = throw NotImplementedError("Add-on installation is not implemented yet.")

    /**
     * Targets every generated value of [T].
     */
    public inline fun <reified T> rule(): RuleTarget<T> = throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets generated values of [Value] whose owner is [Owner], regardless of property name.
     */
    @JvmName("ruleOwnedBy")
    public inline fun <reified Owner, reified Value> rule(): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets values generated for [property].
     */
    public fun <Owner, Value> rule(property: KProperty1<Owner, Value>): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets values generated for [path].
     */
    public fun <Root, Value> rule(path: PropertyPath<Root, Value>): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets generated values of [Value] whose owner is [Owner] and property name is [name].
     */
    @JvmName("ruleOwnedByName")
    public inline fun <reified Owner, reified Value> rule(name: String): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets generated values of [Value] whose owner is [Owner] and property name matches [regex].
     */
    @JvmName("ruleOwnedByName")
    public inline fun <reified Owner, reified Value> rule(regex: Regex): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets generated values of [Value] whose property name is [name], regardless of owner.
     */
    @JvmName("ruleByName")
    public inline fun <reified Value> rule(name: String): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Targets generated values of [Value] whose property name matches [regex], regardless of owner.
     */
    @JvmName("ruleByName")
    public inline fun <reified Value> rule(regex: Regex): RuleTarget<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for properties whose name is this string.
     */
    public infix fun <Value> String.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates properties whose name is this string by invoking [generator].
     */
    public infix fun <Value> String.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for properties whose name matches this regex.
     */
    public infix fun <Value> Regex.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates properties whose name matches this regex by invoking [generator].
     */
    public infix fun <Value> Regex.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates [value] for this property.
     */
    public infix fun <Owner, Value> KProperty1<Owner, Value>.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this property by invoking [generator].
     */
    public infix fun <Owner, Value> KProperty1<Owner, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this property using automatic generation.
     */
    public fun <Owner, Value> KProperty1<Owner, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates each element for this collection property by invoking [generator].
     */
    public infix fun <Owner, Element, CollectionType : Collection<Element>> KProperty1<Owner, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Generates map keys for this property by invoking [generator].
     */
    public infix fun <Owner, Key, Value, MapType : Map<Key, Value>> KProperty1<Owner, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates map values for this property by invoking [generator].
     */
    public infix fun <Owner, Key, Value, MapType : Map<Key, Value>> KProperty1<Owner, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates [value] for this nested property path.
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generates(value: Value): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this nested property path by invoking [generator].
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generatesBy(generator: Generator<Value>): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates this nested property path using automatic generation.
     */
    public fun <Root, Value> PropertyPath<Root, Value>.autoGenerates(): GenerationSpec<Value> =
        throw NotImplementedError("Rule configuration is not implemented yet.")

    /**
     * Generates each element for this nested collection property path by invoking [generator].
     */
    public infix fun <Root, Element, CollectionType : Collection<Element>> PropertyPath<Root, CollectionType>.generatesEach(
        generator: Generator<Element>,
    ): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

    /**
     * Generates map keys for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesKeys(
        generator: Generator<Key>,
    ): MapKeySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")

    /**
     * Generates map values for this nested property path by invoking [generator].
     */
    public infix fun <Root, Key, Value, MapType : Map<Key, Value>> PropertyPath<Root, MapType>.generatesValues(
        generator: Generator<Value>,
    ): MapValueSpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")
}
