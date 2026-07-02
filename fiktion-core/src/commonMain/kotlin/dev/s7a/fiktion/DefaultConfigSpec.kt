package dev.s7a.fiktion

/**
 * Config value selected by the same target matching model as generation rules.
 */
internal data class DefaultConfigSpec<Value : Any>(
    val setting: FiktionConfigSetting<*, Value>,
    val matcher: RuleMatcher,
    val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) {
    val key: FiktionConfig<*, Value>
        get() = setting.key

    fun snapshot(precedence: RulePrecedence? = null): DefaultConfigSpec<Value> = copy(precedence = precedence ?: this.precedence)
}
