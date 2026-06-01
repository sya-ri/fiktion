package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports Fiktion rule declarations inside loops.
 */
public class AvoidRuleDeclarationsInLoops(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid declaring Fiktion rules inside loops.",
    ) {
    private val loopCalls: Set<String> by config(DEFAULT_LOOP_CALLS.toList()) { calls -> calls.toSet() }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return
        val rule = expression.infixRuleDeclaration() ?: return
        if (!expression.isInsideLoop(loopCalls)) return

        reportRuleInLoop(rule)
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return
        val rule = expression.regularCallRuleDeclaration() ?: return
        if (!expression.isInsideLoop(loopCalls)) return

        reportRuleInLoop(rule)
    }

    private fun reportRuleInLoop(rule: RuleDeclaration) {
        report(
            Finding(
                entity = Entity.from(rule.expression),
                message = "Avoid declaring Fiktion rules for `${rule.targetText}` inside loops; use one rule with generator logic instead.",
            ),
        )
    }
}

private data class RuleDeclaration(
    val expression: KtExpression,
    val targetText: String,
)

private fun KtBinaryExpression.infixRuleDeclaration(): RuleDeclaration? {
    val operation =
        (FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using)
            .firstOrNull { operation -> usesInfixOperation(operation) } ?: return null
    val targetCalls =
        when (operation) {
            FiktionOperation.Using -> FIKTION_CONFIG_RULE_TARGET_CALLS
            else -> FIKTION_GENERATOR_RULE_TARGET_CALLS
        }
    val target =
        left?.fiktionRuleTargetText(
            calls = targetCalls,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    return RuleDeclaration(expression = this, targetText = target.sourceText)
}

private fun KtDotQualifiedExpression.regularCallRuleDeclaration(): RuleDeclaration? {
    val selectorCall = selectorExpression as? KtCallExpression ?: return null
    val operation =
        (FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using)
            .firstOrNull { operation -> selectorCall.calleeExpression?.text == operation.text } ?: return null
    val targetCalls =
        when (operation) {
            FiktionOperation.Using -> FIKTION_CONFIG_RULE_TARGET_CALLS
            else -> FIKTION_GENERATOR_RULE_TARGET_CALLS
        }
    val target =
        receiverExpression.fiktionRuleTargetText(
            calls = targetCalls,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    return RuleDeclaration(expression = this, targetText = target.sourceText)
}
