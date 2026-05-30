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
     * Registers [value] for [configKey] on this target.
     */
    fun <Value : Any> config(
        configKey: FiktionConfig<in T, Value>,
        value: Value,
    ): DefaultConfigSpec<Value> {
        val spec = DefaultConfigSpec(key = configKey, matcher = matcher, value = value)
        config.add(spec)
        return spec
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
     * Registers automatic collection generation for this target.
     */
    fun <Element, CollectionType : Collection<Element>> generatesAutomaticCollection(
        elementType: KType,
    ): DefaultAutoCollectionGenerationSpec<Element, CollectionType> {
        val spec =
            DefaultAutoCollectionGenerationSpec<Element, CollectionType>(
                key = key,
                matcher = matcher,
                autoCollectionElementType = elementType,
            )
        config.add(spec)
        return spec
    }

    /**
     * Creates a collection generation spec for this target.
     */
    fun <Element, CollectionType : Collection<Element>> collectionGenerationSpec(
        elementGenerator: FakeContext.() -> Element,
    ): DefaultCollectionGenerationSpec<Element, CollectionType> {
        val spec =
            DefaultCollectionGenerationSpec<Element, CollectionType>(
                key = key,
                matcher = matcher,
                elementGenerator = elementGenerator,
            )
        config.add(spec)
        return spec
    }

    /**
     * Returns shared map generation spec for this target.
     */
    fun <Key, Value, MapType : Map<Key, Value>> mapGenerationSpec(): DefaultMapGenerationSpec<Key, Value, MapType> =
        config.mapGenerationSpec(key) {
            DefaultMapGenerationSpec<Key, Value, MapType>(
                key = key,
                matcher = matcher,
            ).also(config::add)
        }
}
