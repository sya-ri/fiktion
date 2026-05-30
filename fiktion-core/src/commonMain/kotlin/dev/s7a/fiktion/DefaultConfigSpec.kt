package dev.s7a.fiktion

/**
 * Config value selected by the same target matching model as generation rules.
 */
internal data class DefaultConfigSpec<Value : Any>(
    val key: FiktionConfig<*, Value>,
    val matcher: RuleMatcher,
    val value: Value,
    val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) {
    fun snapshot(precedence: RulePrecedence? = null): DefaultConfigSpec<Value> = copy(precedence = precedence ?: this.precedence)
}
