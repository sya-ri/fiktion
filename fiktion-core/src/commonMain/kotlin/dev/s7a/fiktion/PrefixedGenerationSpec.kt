package dev.s7a.fiktion

/**
 * Returns this generation spec scoped below [prefix].
 */
internal fun DefaultGenerationSpec<*>.prefixedBy(prefix: List<PathRuleSegment>): DefaultGenerationSpec<*> =
    DefaultGenerationSpec(
        key = key.prefixedBy(prefix),
        matcher = matcher.prefixedBy(prefix = prefix, allowRootTarget = false),
        generator = generator,
        seed = seed,
        nullProbability = nullProbability,
        defaultProbability = defaultProbability,
        automaticallyGenerates = automaticallyGenerates,
        defaultGenerates = defaultGenerates,
        precedence = precedence,
    )

/**
 * Returns this config spec scoped below [prefix].
 */
internal fun <Value : Any> DefaultConfigSpec<Value>.prefixedBy(prefix: List<PathRuleSegment>): DefaultConfigSpec<Value> =
    DefaultConfigSpec(
        setting = setting,
        matcher = matcher.prefixedBy(prefix = prefix, allowRootTarget = true),
        precedence = precedence,
    )

/**
 * Returns this rule key scoped below [prefix].
 */
private fun RuleKey.prefixedBy(prefix: List<PathRuleSegment>): RuleKey =
    when (this) {
        is RuleKey.Path -> RuleKey.Path(prefix + segments)
        is RuleKey.Name -> RuleKey.Path(prefix + PathRuleSegment(ownerId = null, name = name, valueId = value?.nonNullTypeId()))
        is RuleKey.RegexName -> unsupportedNestedRule("regex name")
        is RuleKey.TypeFamily -> unsupportedNestedRule("type family")
        is RuleKey.Type -> unsupportedNestedRule("type")
        is RuleKey.OwnedType -> unsupportedNestedRule("owner type")
        is RuleKey.Property -> unsupportedNestedRule("owner property")
        is RuleKey.Container -> unsupportedNestedRule("container part")
        is RuleKey.OwnedRegexName -> unsupportedNestedRule("owner regex name")
    }

/**
 * Returns this rule matcher scoped below [prefix].
 */
private fun RuleMatcher.prefixedBy(
    prefix: List<PathRuleSegment>,
    allowRootTarget: Boolean,
): RuleMatcher =
    when (this) {
        is RuleMatcher.All -> {
            if (allowRootTarget) {
                RuleMatcher.Path(prefix)
            } else {
                unsupportedNestedRule("all")
            }
        }

        is RuleMatcher.Path -> {
            RuleMatcher.Path(prefix + segments)
        }

        is RuleMatcher.Name -> {
            RuleMatcher.Path(prefix + PathRuleSegment(ownerId = null, name = name, valueId = value?.nonNullTypeId()))
        }

        is RuleMatcher.RegexName -> {
            unsupportedNestedRule("regex name")
        }

        is RuleMatcher.TypeFamily -> {
            unsupportedNestedRule("type family")
        }

        is RuleMatcher.Type -> {
            if (allowRootTarget) {
                RuleMatcher.Path(prefix)
            } else {
                unsupportedNestedRule("type")
            }
        }

        is RuleMatcher.OwnedType -> {
            unsupportedNestedRule("owner type")
        }

        is RuleMatcher.Property -> {
            unsupportedNestedRule("owner property")
        }

        is RuleMatcher.Container -> {
            unsupportedNestedRule("container part")
        }

        is RuleMatcher.OwnedRegexName -> {
            unsupportedNestedRule("owner regex name")
        }
    }

/**
 * Reports that a rule cannot be safely scoped by nested DSL.
 */
private fun unsupportedNestedRule(target: String): Nothing =
    throw FiktionConfigurationException("Nested Fiktion configuration does not support $target rules.")
