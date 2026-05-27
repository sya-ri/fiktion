package dev.s7a.fiktion.generators

/**
 * Built-in character sets for basic string generation.
 */
public object FiktionCharsets {
    /**
     * Uppercase and lowercase ASCII letters.
     */
    public val Alpha: FiktionCharset =
        FiktionCharset(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
        )

    /**
     * Uppercase and lowercase ASCII letters plus digits.
     */
    public val AlphaNumeric: FiktionCharset =
        FiktionCharset(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        )

    /**
     * ASCII digits.
     */
    public val Numeric: FiktionCharset = FiktionCharset("0123456789")

    /**
     * Lowercase ASCII letters.
     */
    public val LowercaseAlpha: FiktionCharset = FiktionCharset("abcdefghijklmnopqrstuvwxyz")

    /**
     * Uppercase ASCII letters.
     */
    public val UppercaseAlpha: FiktionCharset = FiktionCharset("ABCDEFGHIJKLMNOPQRSTUVWXYZ")

    /**
     * Lowercase ASCII letters plus digits.
     */
    public val LowercaseAlphaNumeric: FiktionCharset =
        FiktionCharset(
            "abcdefghijklmnopqrstuvwxyz0123456789",
        )

    /**
     * Uppercase ASCII letters plus digits.
     */
    public val UppercaseAlphaNumeric: FiktionCharset =
        FiktionCharset(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        )
}
