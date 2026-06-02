package dev.s7a.fiktion

/**
 * Default mutable type-family rule target implementation.
 */
internal class DefaultTypeFamilyRuleTarget<T>(
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
) : TypeFamilyRuleTarget<T> {
    /**
     * Registers [generator] for this type-family target.
     */
    fun generatesBy(generator: TypeFamilyGenerator<T>): DefaultTypeFamilyGenerationSpec<T> {
        val spec = DefaultTypeFamilyGenerationSpec(key = key, matcher = matcher, typeFamilyGenerator = generator)
        config.add(spec)
        return spec
    }

    /**
     * Registers automatic generation for this type-family target.
     */
    fun generatesAutomatically(): DefaultGenerationSpec<T> {
        val spec = DefaultGenerationSpec<T>(key = key, matcher = matcher, automaticallyGenerates = true)
        config.add(spec)
        return spec
    }
}
