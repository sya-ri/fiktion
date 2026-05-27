package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext
import kotlin.reflect.KType

/**
 * Default mutable name target implementation.
 */
internal class DefaultRuleNameTarget(
    /**
     * Creates the replacement key for the inferred value type.
     */
    private val key: (KType) -> RuleKey,
    /**
     * Creates the runtime matcher for the inferred value type.
     */
    private val matcher: (KType) -> RuleMatcher,
    /**
     * Callback that stores the registered rule.
     */
    private val register: (RegisteredRule<*>) -> Unit,
) : RuleNameTarget {
    /**
     * Registers [generator] for [type].
     */
    @Deprecated("Use the reified generates or generatesBy overload.", level = DeprecationLevel.ERROR)
    override fun generates(
        type: KType,
        generator: FakeContext.() -> Any?,
    ): GenerationSpec<*> {
        val rule = RegisteredRule(key = key(type), matcher = matcher(type), generator = generator)
        register(rule)
        return rule
    }
}
