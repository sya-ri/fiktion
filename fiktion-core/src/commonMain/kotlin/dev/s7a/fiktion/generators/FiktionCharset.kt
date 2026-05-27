package dev.s7a.fiktion.generators

/**
 * Character set used by string generators.
 */
public class FiktionCharset public constructor(
    /**
     * Characters available for generated strings.
     */
    public val chars: String,
) {
    /**
     * Creates a character set from [chars].
     *
     * The iterable is consumed once and stored as a string for indexed access during generation.
     */
    public constructor(chars: Iterable<Char>) : this(chars.joinToString(separator = ""))

    /**
     * Returns a character set containing this set followed by [other].
     */
    public operator fun plus(other: FiktionCharset): FiktionCharset = FiktionCharset(chars + other.chars)

    /**
     * Returns a character set containing this set followed by [chars].
     */
    public operator fun plus(chars: Iterable<Char>): FiktionCharset = this + FiktionCharset(chars)

    /**
     * Built-in character sets for basic string generation.
     */
    public companion object {
        /**
         * Lowercase ASCII letters.
         */
        public val LowercaseAlpha: FiktionCharset = FiktionCharset('a'..'z')

        /**
         * Uppercase ASCII letters.
         */
        public val UppercaseAlpha: FiktionCharset = FiktionCharset('A'..'Z')

        /**
         * ASCII digits.
         */
        public val Numeric: FiktionCharset = FiktionCharset('0'..'9')

        /**
         * Uppercase and lowercase ASCII letters.
         */
        public val Alpha: FiktionCharset = LowercaseAlpha + UppercaseAlpha

        /**
         * Lowercase ASCII letters plus digits.
         */
        public val LowercaseAlphaNumeric: FiktionCharset = LowercaseAlpha + Numeric

        /**
         * Uppercase ASCII letters plus digits.
         */
        public val UppercaseAlphaNumeric: FiktionCharset = UppercaseAlpha + Numeric

        /**
         * Uppercase and lowercase ASCII letters plus digits.
         */
        public val AlphaNumeric: FiktionCharset = Alpha + Numeric
    }
}
