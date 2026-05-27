package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext

/**
 * Registered generation rule and its matching metadata.
 */
internal data class RegisteredRule<T>(
    /**
     * Stable key used for replacement within the same layer.
     */
    val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    val matcher: RuleMatcher,
    /**
     * Generator executed when [matcher] selects this rule.
     */
    val generator: FakeContext.() -> T,
    /**
     * Optional seed override for this rule.
     */
    var seed: Long? = null,
    /**
     * Probability of generating `null` for this rule.
     */
    var nullProbability: Probability? = null,
    /**
     * Probability of using the default value for this rule.
     */
    var defaultProbability: Probability? = null,
    /**
     * Precedence layer assigned while composing configurations.
     */
    val precedence: RulePrecedence = RulePrecedence.GLOBAL,
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
    fun snapshot(precedence: RulePrecedence = this.precedence): RegisteredRule<T> = copy(precedence = precedence)
}
