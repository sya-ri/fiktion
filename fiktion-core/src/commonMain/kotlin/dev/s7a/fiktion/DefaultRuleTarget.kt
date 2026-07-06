package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Default mutable rule target implementation.
 */
internal class DefaultRuleTarget<T>(
    /**
     * Mutable configuration receiving rules and target-specific specs.
     */
    private val config: MutableFiktionConfig,
    /**
     * Stable key used for replacement within the same layer.
     */
    private val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    private val matcher: RuleMatcher,
    /**
     * Type selected by this target, if it is known.
     */
    private val targetType: KType? = null,
) : RuleTarget<T> {
    /**
     * Targets a container part generated for the value selected by this target.
     */
    fun <Element> containerPartTarget(
        kind: ContainerPart.Kind,
        resultType: (KType) -> KType?,
    ): RuleTarget<Element> {
        val containerType =
            targetType
                ?: throw FiktionConfigurationException("Cannot target a nested container part because the container type is unavailable.")
        val part = ContainerPart(kind = kind, container = containerType)
        val parts =
            when (matcher) {
                is RuleMatcher.Container -> matcher.parts + part
                else -> listOf(part)
            }
        return DefaultRuleTarget(
            config = config,
            key = RuleKey.Container(parts = parts),
            matcher = RuleMatcher.Container(parts = parts),
            targetType = resultType(containerType),
        )
    }

    /**
     * Registers [generator] for this target.
     */
    fun generatesBy(generator: FakeContext.() -> T): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec(key = key, matcher = matcher, generator = generator)
        config.add(spec)
        return spec
    }

    /**
     * Registers candidate selection for this target.
     */
    fun generatesOneOf(values: List<T>): DefaultOneOfGenerationSpec<T> {
        val spec = DefaultOneOfGenerationSpec(key = key, matcher = matcher, values = values)
        config.add(spec)
        return spec
    }

    /**
     * Registers [value] for [configKey] on this target.
     */
    fun <Value : Any> config(
        configKey: FiktionConfig<in T, Value>,
        value: Value,
    ): DefaultConfigSpec<Value> = config(configKey(value))

    /**
     * Registers [setting] on this target.
     */
    fun <Value : Any> config(setting: FiktionConfigSetting<in T, Value>): DefaultConfigSpec<Value> {
        val spec = DefaultConfigSpec(setting = setting, matcher = matcher)
        config.add(spec)
        return spec
    }

    /**
     * Registers [settings] on this target.
     */
    fun config(settings: FiktionConfigSettingGroup<in T>) {
        settings.settings.forEach { setting ->
            config.add(DefaultConfigSpec(setting = setting, matcher = matcher))
        }
    }

    /**
     * Registers automatic generation for this target.
     */
    fun generatesAutomatically(): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec<T>(key = key, matcher = matcher, automaticallyGenerates = true)
        config.add(spec)
        return spec
    }

    /**
     * Registers constructor default generation for this target.
     */
    fun generatesDefault(): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec<T>(key = key, matcher = matcher, defaultGenerates = true)
        config.add(spec)
        return spec
    }
}
