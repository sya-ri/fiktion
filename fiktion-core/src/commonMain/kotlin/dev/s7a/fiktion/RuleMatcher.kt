package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Runtime predicate for selecting rules for a generation request.
 */
internal sealed interface RuleMatcher {
    /**
     * Relative specificity within the same precedence layer.
     */
    val specificity: RuleSpecificity

    /**
     * Returns whether this matcher applies to [request].
     */
    fun matches(request: GenerationRequest): Boolean

    /**
     * Matches any generated value.
     */
    data object All : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.ALL

        override fun matches(request: GenerationRequest): Boolean = true
    }

    /**
     * Matches by generated type classifier.
     */
    data class TypeFamily(
        /**
         * Matched generated type family.
         */
        val type: KType,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.TYPE_FAMILY

        /**
         * Returns true when the requested type belongs to the same type family as [type].
         */
        override fun matches(request: GenerationRequest): Boolean =
            request.type.classifier == type.classifier && request.type.isMarkedNullable == type.isMarkedNullable
    }

    /**
     * Matches by generated type.
     */
    data class Type(
        /**
         * Matched generated type.
         */
        val type: KType,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.TYPE

        /**
         * Returns true when the requested type exactly matches [type].
         */
        override fun matches(request: GenerationRequest): Boolean = request.type == type
    }

    /**
     * Matches by owner, property name, and value type.
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
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.OWNER_NAME

        /**
         * Returns true when owner, property name, and value type all match.
         */
        override fun matches(request: GenerationRequest): Boolean =
            request.owner == owner && request.propertyName == name && request.type == value
    }

    /**
     * Matches by owner and value type regardless of property name.
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
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.OWNER_TYPE

        /**
         * Returns true when owner and value type match.
         */
        override fun matches(request: GenerationRequest): Boolean = request.owner == owner && request.type == value
    }

    /**
     * Matches by exact nested property path.
     */
    data class Path(
        /**
         * Properties from root to target.
         */
        val segments: List<PathRuleSegment>,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.PATH

        /**
         * Returns true when the full property path exactly matches [segments].
         */
        override fun matches(request: GenerationRequest): Boolean =
            request.pathSegments.size == segments.size &&
                segments.zip(request.pathSegments).all { (ruleSegment, requestSegment) ->
                    ruleSegment.matches(requestSegment)
                }
    }

    /**
     * Matches values generated as nested container parts.
     */
    data class Container(
        /**
         * Container parts from outermost to innermost.
         */
        val parts: List<ContainerPart>,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.CONTAINER_PART

        /**
         * Returns true when the request is for the container part below [parts].
         */
        override fun matches(request: GenerationRequest): Boolean = request.containerParts == parts
    }

    /**
     * Matches by property name and optional value type.
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
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.NAME

        /**
         * Returns true when the property name matches and [value] is absent or matches the requested type.
         */
        override fun matches(request: GenerationRequest): Boolean = request.propertyName == name && (value == null || request.type == value)
    }

    /**
     * Matches by property-name regex and optional value type.
     */
    data class RegexName(
        /**
         * Property-name regex.
         */
        val regex: Regex,
        /**
         * Optional value type constraint.
         */
        val value: KType?,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.NAME

        /**
         * Returns true when the property name matches [regex] and [value] is absent or matches the requested type.
         */
        override fun matches(request: GenerationRequest): Boolean =
            request.propertyName?.let(regex::matches) == true && (value == null || request.type == value)
    }

    /**
     * Matches by owner, property-name regex, and value type.
     */
    data class OwnedRegexName(
        /**
         * Type that owns generated values.
         */
        val owner: KType,
        /**
         * Property-name regex.
         */
        val regex: Regex,
        /**
         * Generated value type.
         */
        val value: KType,
    ) : RuleMatcher {
        override val specificity: RuleSpecificity = RuleSpecificity.OWNER_NAME

        /**
         * Returns true when owner, property-name regex, and value type all match.
         */
        override fun matches(request: GenerationRequest): Boolean =
            request.owner == owner && request.propertyName?.let(regex::matches) == true && request.type == value
    }
}

/**
 * Returns whether this rule segment matches [requestSegment].
 */
private fun PathRuleSegment.matches(requestSegment: PathRuleSegment): Boolean =
    name == requestSegment.name &&
        (ownerId == null || ownerId == requestSegment.ownerId) &&
        (valueId == null || valueId == requestSegment.valueId)
