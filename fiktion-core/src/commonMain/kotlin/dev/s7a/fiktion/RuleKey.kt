package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Stable identity for replacing rules.
 */
internal sealed interface RuleKey {
    /**
     * Key for a type-wide rule.
     */
    data class Type(
        /**
         * Matched generated type.
         */
        val type: KType,
    ) : RuleKey

    /**
     * Key for an owner/name/value property rule.
     */
    data class Property(
        /**
         * Type that owns the property.
         */
        val owner: KType,
        /**
         * Property name.
         */
        val name: String,
        /**
         * Property value type.
         */
        val value: KType,
    ) : RuleKey

    /**
     * Key for an owner/value rule regardless of property name.
     */
    data class OwnedType(
        /**
         * Type that owns generated values.
         */
        val owner: KType,
        /**
         * Generated value type.
         */
        val value: KType,
    ) : RuleKey

    /**
     * Key for a specific nested property path.
     */
    data class Path(
        /**
         * Properties from root to target.
         */
        val segments: List<PathRuleSegment>,
    ) : RuleKey

    /**
     * Key for a property-name rule.
     */
    data class Name(
        /**
         * Property name.
         */
        val name: String,
        /**
         * Optional value type constraint.
         */
        val value: KType?,
    ) : RuleKey

    /**
     * Key for a regex property-name rule.
     */
    data class RegexName(
        /**
         * Regex pattern string.
         */
        val pattern: String,
        /**
         * Regex options.
         */
        val options: Set<RegexOption>,
        /**
         * Optional value type constraint.
         */
        val value: KType?,
    ) : RuleKey

    /**
     * Key for an owner-qualified regex property-name rule.
     */
    data class OwnedRegexName(
        /**
         * Type that owns generated values.
         */
        val owner: KType,
        /**
         * Regex pattern string.
         */
        val pattern: String,
        /**
         * Regex options.
         */
        val options: Set<RegexOption>,
        /**
         * Generated value type.
         */
        val value: KType,
    ) : RuleKey
}
