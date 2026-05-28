package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext
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
) : RuleTarget<T> {
    /**
     * Registers [generator] for this target.
     */
    fun generatesBy(generator: FakeContext.() -> T): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec(key = key, matcher = matcher, generator = generator)
        config.add(spec)
        return spec
    }

    /**
     * Registers automatic generation for this target.
     */
    fun autoGenerates(): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec<T>(key = key, matcher = matcher, automaticallyGenerates = true)
        config.add(spec)
        return spec
    }

    /**
     * Registers automatic collection generation for this target.
     */
    fun <Element, CollectionType : Collection<Element>> autoGeneratesCollection(
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
