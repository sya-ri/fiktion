package dev.s7a.fiktion

/**
 * Returns the effective configuration for [fiktion], including global rules.
 */
internal fun configOf(fiktion: Fiktion): FiktionConfig =
    GlobalFiktion.config.overlaidBy(
        other = (fiktion as DefaultFiktion).config,
        rulePrecedence = RulePrecedence.INSTANCE,
    )
