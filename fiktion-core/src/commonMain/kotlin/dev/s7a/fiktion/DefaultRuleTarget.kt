package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext

/**
 * Default mutable rule target implementation.
 */
internal class DefaultRuleTarget<T>(
    /**
     * Callback that registers a generator and returns the registered rule.
     */
    private val register: (FakeContext.() -> T) -> RegisteredRule<T>,
) : RuleTarget<T> {
    /**
     * Registers [generator] for this target.
     */
    fun generatesBy(generator: FakeContext.() -> T): RegisteredRule<T> = register(generator)
}
