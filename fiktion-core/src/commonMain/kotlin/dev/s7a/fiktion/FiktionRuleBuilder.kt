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
     * Generates this property using its constructor default value.
     */
    @Suppress("DEPRECATION_ERROR", "UNUSED_PARAMETER")
    public inline infix fun <reified Owner, reified Value> KProperty1<Owner, Value>.generates(default: Default): GenerationSpec<Value> =
        property(property = this, owner = typeOf<Owner>(), value = typeOf<Value>()) generates default

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
     * Generates this nested property path using its constructor default value.
     */
    public infix fun <Root, Value> PropertyPath<Root, Value>.generates(default: Default): GenerationSpec<Value> =
        property(this) generates default

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
     * Targets this property with values generated from dependencies in declaration order.
     */
    public inline fun <reified Owner, reified Value> KProperty1<Owner, Value>.dependsOn(
        vararg dependencies: KProperty1<Owner, *>,
    ): DependentRuleTarget<Value> =
        dependencyTarget(
            property = this,
            owner = typeOf<Owner>(),
            value = typeOf<Value>(),
            dependencies = dependencies.toList(),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <reified Owner, reified Value, reified D1> KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
    ): DependentRuleTarget1<Value, D1> = DependentRuleTarget1(this.dependsOn(*arrayOf(dependency1)))

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <reified Owner, reified Value, reified D1, reified D2> KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
    ): DependentRuleTarget2<Value, D1, D2> = DependentRuleTarget2(this.dependsOn(*arrayOf(dependency1, dependency2)))

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <reified Owner, reified Value, reified D1, reified D2, reified D3> KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
    ): DependentRuleTarget3<Value, D1, D2, D3> = DependentRuleTarget3(this.dependsOn(*arrayOf(dependency1, dependency2, dependency3)))

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <reified Owner, reified Value, reified D1, reified D2, reified D3, reified D4> KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
    ): DependentRuleTarget4<Value, D1, D2, D3, D4> =
        DependentRuleTarget4(this.dependsOn(*arrayOf(dependency1, dependency2, dependency3, dependency4)))

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
    ): DependentRuleTarget5<Value, D1, D2, D3, D4, D5> =
        DependentRuleTarget5(this.dependsOn(*arrayOf(dependency1, dependency2, dependency3, dependency4, dependency5)))

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
    ): DependentRuleTarget6<Value, D1, D2, D3, D4, D5, D6> =
        DependentRuleTarget6(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
    ): DependentRuleTarget7<Value, D1, D2, D3, D4, D5, D6, D7> =
        DependentRuleTarget7(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
    ): DependentRuleTarget8<Value, D1, D2, D3, D4, D5, D6, D7, D8> =
        DependentRuleTarget8(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
    ): DependentRuleTarget9<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9> =
        DependentRuleTarget9(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
    ): DependentRuleTarget10<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10> =
        DependentRuleTarget10(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
    ): DependentRuleTarget11<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11> =
        DependentRuleTarget11(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
    ): DependentRuleTarget12<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12> =
        DependentRuleTarget12(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
    ): DependentRuleTarget13<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13> =
        DependentRuleTarget13(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
    ): DependentRuleTarget14<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14> =
        DependentRuleTarget14(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
    ): DependentRuleTarget15<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15> =
        DependentRuleTarget15(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
    ): DependentRuleTarget16<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16> =
        DependentRuleTarget16(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
    ): DependentRuleTarget17<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17> =
        DependentRuleTarget17(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
        reified D18,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
        dependency18: KProperty1<Owner, D18>,
    ): DependentRuleTarget18<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18> =
        DependentRuleTarget18(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                    dependency18,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
        reified D18,
        reified D19,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
        dependency18: KProperty1<Owner, D18>,
        dependency19: KProperty1<Owner, D19>,
    ): DependentRuleTarget19<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19> =
        DependentRuleTarget19(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                    dependency18,
                    dependency19,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
        reified D18,
        reified D19,
        reified D20,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
        dependency18: KProperty1<Owner, D18>,
        dependency19: KProperty1<Owner, D19>,
        dependency20: KProperty1<Owner, D20>,
    ): DependentRuleTarget20<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20> =
        DependentRuleTarget20(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                    dependency18,
                    dependency19,
                    dependency20,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
        reified D18,
        reified D19,
        reified D20,
        reified D21,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
        dependency18: KProperty1<Owner, D18>,
        dependency19: KProperty1<Owner, D19>,
        dependency20: KProperty1<Owner, D20>,
        dependency21: KProperty1<Owner, D21>,
    ): DependentRuleTarget21<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21> =
        DependentRuleTarget21(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                    dependency18,
                    dependency19,
                    dependency20,
                    dependency21,
                ),
            ),
        )

    /**
     * Targets this property with values generated from typed dependencies in declaration order.
     */
    public inline fun <
        reified Owner,
        reified Value,
        reified D1,
        reified D2,
        reified D3,
        reified D4,
        reified D5,
        reified D6,
        reified D7,
        reified D8,
        reified D9,
        reified D10,
        reified D11,
        reified D12,
        reified D13,
        reified D14,
        reified D15,
        reified D16,
        reified D17,
        reified D18,
        reified D19,
        reified D20,
        reified D21,
        reified D22,
    > KProperty1<Owner, Value>.dependsOn(
        dependency1: KProperty1<Owner, D1>,
        dependency2: KProperty1<Owner, D2>,
        dependency3: KProperty1<Owner, D3>,
        dependency4: KProperty1<Owner, D4>,
        dependency5: KProperty1<Owner, D5>,
        dependency6: KProperty1<Owner, D6>,
        dependency7: KProperty1<Owner, D7>,
        dependency8: KProperty1<Owner, D8>,
        dependency9: KProperty1<Owner, D9>,
        dependency10: KProperty1<Owner, D10>,
        dependency11: KProperty1<Owner, D11>,
        dependency12: KProperty1<Owner, D12>,
        dependency13: KProperty1<Owner, D13>,
        dependency14: KProperty1<Owner, D14>,
        dependency15: KProperty1<Owner, D15>,
        dependency16: KProperty1<Owner, D16>,
        dependency17: KProperty1<Owner, D17>,
        dependency18: KProperty1<Owner, D18>,
        dependency19: KProperty1<Owner, D19>,
        dependency20: KProperty1<Owner, D20>,
        dependency21: KProperty1<Owner, D21>,
        dependency22: KProperty1<Owner, D22>,
    ): DependentRuleTarget22<Value, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22> =
        DependentRuleTarget22(
            this.dependsOn(
                *arrayOf(
                    dependency1,
                    dependency2,
                    dependency3,
                    dependency4,
                    dependency5,
                    dependency6,
                    dependency7,
                    dependency8,
                    dependency9,
                    dependency10,
                    dependency11,
                    dependency12,
                    dependency13,
                    dependency14,
                    dependency15,
                    dependency16,
                    dependency17,
                    dependency18,
                    dependency19,
                    dependency20,
                    dependency21,
                    dependency22,
                ),
            ),
        )

    /**
     * Configures how generated elements are materialized as collection type [CollectionType].
     */
    @Suppress("DEPRECATION_ERROR")
    public inline fun <reified CollectionType : Collection<*>> configureCollection(
        unique: Boolean = false,
        noinline convert: (List<Any?>) -> CollectionType,
    ) {
        configureCollection(type = typeOf<CollectionType>(), convert = convert, unique = unique)
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
    @Deprecated(
        "Use the reified configureCollection<CollectionType>(convert) overload. This overload will be removed in 1.0.0.",
        level = DeprecationLevel.ERROR,
    )
    public fun configureCollection(
        type: KType,
        convert: (List<Any?>) -> Collection<*>,
    ) {
        mutableConfig.add(CollectionConverter(classifier = type.classifier, convert = convert, unique = false))
    }

    /**
     * Configures how generated elements are materialized as collection type [type].
     *
     * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and
     * the converter type consistent.
     */
    @Deprecated("Use the reified configureCollection<CollectionType>(unique, convert) overload.", level = DeprecationLevel.ERROR)
    public fun configureCollection(
        type: KType,
        convert: (List<Any?>) -> Collection<*>,
        unique: Boolean,
    ) {
        mutableConfig.add(CollectionConverter(classifier = type.classifier, convert = convert, unique = unique))
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
     * Creates a dependent rule target using [property] for replacement and lookup.
     */
    @PublishedApi
    internal fun <Owner, Value> dependencyTarget(
        property: KProperty1<Owner, Value>,
        owner: KType,
        value: KType,
        dependencies: List<KProperty1<Owner, *>>,
    ): DependentRuleTarget<Value> =
        DependentRuleTarget(
            DefaultDependentRuleTarget(
                config = mutableConfig,
                key = RuleKey.Property(owner, property.name, value),
                matcher = RuleMatcher.Property(owner, property.name, value),
                dependencies =
                    dependencies.map { dependency ->
                        DependentProperty(owner = owner, name = dependency.name, value = typeOf<Any?>())
                    },
            ),
        )

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
