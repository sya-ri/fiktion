package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext

/**
 * Mutable generation spec registered in a Fiktion configuration.
 */
internal open class DefaultGenerationSpec<T>(
    /**
     * Stable key used for replacement within the same layer.
     */
    open val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    open val matcher: RuleMatcher,
    /**
     * Generator executed when [matcher] selects this spec.
     */
    open val generator: FakeContext.() -> T = { error("Generation spec has no generator.") },
    /**
     * Optional seed override for this generation spec.
     */
    open var seed: Long? = null,
    /**
     * Probability of generating `null` for this generation spec.
     */
    open var nullProbability: Probability? = null,
    /**
     * Probability of using the default value for this generation spec.
     */
    open var defaultProbability: Probability? = null,
    /**
     * Whether this spec should use automatic generation instead of an explicit generator.
     */
    open val automaticallyGenerates: Boolean = false,
    /**
     * Precedence layer assigned while composing configurations.
     */
    open val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) : GenerationSpec<T> {
    override fun withSeed(seed: Long): GenerationSpec<T> {
        this.seed = seed
        return this
    }

    override fun orNullAt(probability: Probability): GenerationSpec<T?> {
        nullProbability = probability
        return this
    }

    override fun orDefaultAt(probability: Probability): GenerationSpec<T> {
        defaultProbability = probability
        return this
    }

    /**
     * Returns a detached copy that can be stored in an immutable configuration snapshot.
     */
    open fun snapshot(precedence: RulePrecedence = this.precedence): DefaultGenerationSpec<T> =
        DefaultGenerationSpec(
            key = key,
            matcher = matcher,
            generator = generator,
            seed = seed,
            nullProbability = nullProbability,
            defaultProbability = defaultProbability,
            automaticallyGenerates = automaticallyGenerates,
            precedence = precedence,
        )
}
