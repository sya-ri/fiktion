package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtThisExpression

/**
 * Reports Fiktion rule targets that do not explicitly target a property.
 */
public class AvoidNonPropertyRuleTargets(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid Fiktion rule targets that do not explicitly target a property.",
    ) {
    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return
        if (!expression.usesInfixOperation(RESTRICTED_OPERATIONS)) return
        val target = expression.left ?: return
        if (target is KtThisExpression) return
        if (target.ruleTargetKind() != RuleTargetKind.NON_PROPERTY) return

        reportTarget(expression = expression, target = target)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return
        if (expression.isNonPropertyTargetBlock()) {
            reportTarget(expression = expression, target = expression)
            return
        }

        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return
        if (expression.calleeExpression?.text !in RESTRICTED_OPERATION_NAMES) return
        val target = qualified.receiverExpression
        if (target.ruleTargetKind() != RuleTargetKind.NON_PROPERTY) return

        reportTarget(expression = qualified, target = target)
    }

    private fun reportTarget(
        expression: KtExpression,
        target: KtExpression,
    ) {
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use an explicit property target instead of `${target.text}`.",
            ),
        )
    }
}

private enum class RuleTargetKind {
    PROPERTY,
    NON_PROPERTY,
    UNKNOWN,
}

private fun KtCallExpression.isNonPropertyTargetBlock(): Boolean =
    lambdaArguments.isNotEmpty() && ruleTargetKind() == RuleTargetKind.NON_PROPERTY

private fun KtExpression.ruleTargetKind(): RuleTargetKind =
    when (this) {
        is KtParenthesizedExpression -> {
            expression?.ruleTargetKind() ?: RuleTargetKind.UNKNOWN
        }

        is KtCallableReferenceExpression -> {
            RuleTargetKind.PROPERTY
        }

        is KtCallExpression -> {
            callRuleTargetKind()
        }

        is KtDotQualifiedExpression -> {
            dotQualifiedRuleTargetKind()
        }

        is KtBinaryExpression -> {
            pathRuleTargetKind()
        }

        else -> {
            RuleTargetKind.UNKNOWN
        }
    }

private fun KtCallExpression.callRuleTargetKind(): RuleTargetKind =
    when (calleeExpression?.text) {
        FiktionCall.Property.text -> RuleTargetKind.PROPERTY
        FiktionCall.Type.text, FiktionCall.TypeFamily.text, FiktionCall.Name.text -> RuleTargetKind.NON_PROPERTY
        else -> RuleTargetKind.UNKNOWN
    }

private fun KtDotQualifiedExpression.dotQualifiedRuleTargetKind(): RuleTargetKind {
    val selectorCall = selectorExpression as? KtCallExpression
    if (selectorCall != null) return selectorCall.callRuleTargetKind()
    return RuleTargetKind.UNKNOWN
}

private fun KtBinaryExpression.pathRuleTargetKind(): RuleTargetKind {
    if (operationReference.text != PROPERTY_PATH_OPERATION) return RuleTargetKind.UNKNOWN
    return if (left?.ruleTargetKind() == RuleTargetKind.PROPERTY && right?.ruleTargetKind() == RuleTargetKind.PROPERTY) {
        RuleTargetKind.PROPERTY
    } else {
        RuleTargetKind.UNKNOWN
    }
}

private val RESTRICTED_OPERATIONS = FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using

private val RESTRICTED_OPERATION_NAMES = RESTRICTED_OPERATIONS.map { operation -> operation.text }.toSet()

private const val PROPERTY_PATH_OPERATION = "/"
