package dev.s7a.fiktion.detekt

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

/**
 * Provides Fiktion-specific detekt rules.
 */
public class FiktionRuleSetProvider : RuleSetProvider {
    override val ruleSetId: RuleSetId = RuleSetId("fiktion")

    override fun instance(): RuleSet =
        RuleSet(
            id = ruleSetId,
            rules =
                listOf(
                    ::AvoidContainerPartOnStarProjectedUnknown,
                    ::AvoidEmptyOneOf,
                    ::AvoidGlobalConfigureInLocalTest,
                    ::AvoidGlobalFiktionConfigureWithoutRestore,
                    ::AvoidInvalidConfigRange,
                    ::AvoidInvalidTypeFamilyFakeIndex,
                    ::AvoidMultipleConfigsForRuleTarget,
                    ::AvoidMultipleGeneratorsForRuleTarget,
                    ::AvoidMultipleSeedsInFakeSpec,
                    ::AvoidNonPropertyRuleTargets,
                    ::AvoidRandomInstanceInGenerator,
                    ::AvoidRecursiveFakeInGenerator,
                    ::AvoidRuleDeclarationsInLoops,
                    ::AvoidUnusedFiktionSnapshot,
                    ::ForbiddenGeneratorCall,
                    ::PreferContainerConfigureHelpers,
                    ::PreferContainerPartFakeHelpers,
                    ::PreferExplicitFakeSeedName,
                    ::PreferFixedConfigValue,
                    ::PreferGeneratesByForMutableValues,
                    ::PreferGeneratesForFixedValue,
                    ::PreferGeneratesInForRange,
                    ::PreferGeneratesOneOf,
                    ::PreferGroupedRuleTarget,
                    ::PreferIndexedFakeInTypeFamilyLoop,
                    ::PreferInfixFiktionDsl,
                    ::PreferNamedTypeFamilyFakeArgumentIndex,
                    ::PreferPropertyShorthand,
                    ::PreferRuleTargetDeclarationOrder,
                    ::PreferSeedParameter,
                    ::PreferThisUsingInFakeSpec,
                    ::PreferTypeRuleForNonGeneric,
                ),
        )
}
