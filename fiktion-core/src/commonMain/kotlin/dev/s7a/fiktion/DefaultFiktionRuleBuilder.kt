package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

/**
 * Internal implementation for rule registration shared by Fiktion builders.
 */
internal open class DefaultFiktionRuleBuilder(
    /**
     * Mutable configuration receiving registered rules.
     */
    protected val config: MutableFiktionConfig,
) : FiktionRuleBuilder {
    /**
     * Targets every generated value of [type].
     */
    @Deprecated("Use the reified type<T>() overload.", level = DeprecationLevel.ERROR)
    override fun type(type: KType): RuleTarget<*> = target<Any?>(RuleKey.Type(type), RuleMatcher.Type(type))

    /**
     * Targets generated values of [value] whose owner is [owner], regardless of property name.
     */
    @Deprecated("Use the reified property<Owner, Value>() overload.", level = DeprecationLevel.ERROR)
    override fun property(
        owner: KType,
        value: KType,
    ): RuleTarget<*> = target<Any?>(RuleKey.OwnedType(owner, value), RuleMatcher.OwnedType(owner, value))

    @Deprecated("Use property<Owner, Value>(property.name) or the infix KProperty generates API.", level = DeprecationLevel.ERROR)
    override fun <Owner, Value> property(property: KProperty1<Owner, Value>): RuleTarget<Value> =
        target(listOf(PathRuleSegment(ownerId = null, name = property.name, valueId = null)))

    override fun <Root, Value> property(path: PropertyPath<Root, Value>): RuleTarget<Value> = target(path.segments)

    /**
     * Targets generated values of [value] whose owner is [owner] and property name is [name].
     */
    @Deprecated("Use the reified property<Owner, Value>(name) overload.", level = DeprecationLevel.ERROR)
    override fun property(
        owner: KType,
        name: String,
        value: KType,
    ): RuleTarget<*> = target<Any?>(RuleKey.Property(owner, name, value), RuleMatcher.Property(owner, name, value))

    /**
     * Targets generated values of [value] whose owner is [owner] and property name matches [regex].
     */
    @Deprecated("Use the reified property<Owner, Value>(regex) overload.", level = DeprecationLevel.ERROR)
    override fun property(
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
     */
    @Deprecated("Use the reified name<Value>(name) overload.", level = DeprecationLevel.ERROR)
    override fun name(
        name: String,
        value: KType,
    ): RuleTarget<*> = target<Any?>(RuleKey.Name(name, value), RuleMatcher.Name(name, value))

    /**
     * Targets generated values of [value] whose property name matches [regex], regardless of owner.
     */
    @Deprecated("Use the reified name<Value>(regex) overload.", level = DeprecationLevel.ERROR)
    override fun name(
        regex: Regex,
        value: KType,
    ): RuleTarget<*> = target<Any?>(RuleKey.RegexName(regex.pattern, regex.options, value), RuleMatcher.RegexName(regex, value))

    override fun name(name: String): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.Name(name, type) },
            matcher = { type -> RuleMatcher.Name(name, type) },
            register = config::add,
        )

    override fun name(regex: Regex): RuleNameTarget =
        DefaultRuleNameTarget(
            key = { type -> RuleKey.RegexName(regex.pattern, regex.options, type) },
            matcher = { type -> RuleMatcher.RegexName(regex, type) },
            register = config::add,
        )

    /**
     * Targets a property path represented as raw Kotlin properties.
     */
    private fun <Value> target(segments: List<PathRuleSegment>): RuleTarget<Value> =
        target(RuleKey.Path(segments), RuleMatcher.Path(segments))

    /**
     * Registers a rule target using [key] for replacement and [matcher] for lookup.
     */
    private fun <Value> target(
        key: RuleKey,
        matcher: RuleMatcher,
    ): RuleTarget<Value> =
        DefaultRuleTarget { generator: FakeContext.() -> Value ->
            val rule = RegisteredRule(key = key, matcher = matcher, generator = generator)
            config.add(rule)
            rule
        }
}
