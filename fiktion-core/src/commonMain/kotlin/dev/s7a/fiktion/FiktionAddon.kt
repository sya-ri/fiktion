package dev.s7a.fiktion

/**
 * Extension point that contributes reusable Fiktion rules.
 */
public interface FiktionAddon {
    /**
     * Stable add-on identifier used for diagnostics and compatibility checks.
     */
    public val id: String

    /**
     * Installs this add-on into [builder].
     */
    public fun install(builder: FiktionAddonBuilder)
}
