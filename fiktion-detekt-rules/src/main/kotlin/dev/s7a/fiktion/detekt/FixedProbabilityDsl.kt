package dev.s7a.fiktion.detekt

import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNullableType

internal data class FixedProbabilityGeneratesExpression(
    val expression: KtExpression,
    val target: KtExpression,
    val valueText: String,
)

internal fun KtExpression.fixedProbabilityGeneratesExpression(): FixedProbabilityGeneratesExpression? =
    infixFixedProbabilityGeneratesExpression() ?: regularCallFixedProbabilityGeneratesExpression()

private fun KtExpression.infixFixedProbabilityGeneratesExpression(): FixedProbabilityGeneratesExpression? {
    val expression = this as? KtBinaryExpression ?: return null
    if (!expression.usesInfixOperation(FiktionOperation.Generates)) return null
    val target = expression.left?.takeIf { it.fixedValueGeneratesTargetText() != null } ?: return null
    val value = expression.right ?: return null
    return FixedProbabilityGeneratesExpression(expression = expression, target = target, valueText = value.text)
}

private fun KtExpression.regularCallFixedProbabilityGeneratesExpression(): FixedProbabilityGeneratesExpression? {
    val expression = this as? KtDotQualifiedExpression ?: return null
    val call = expression.selectorExpression as? KtCallExpression ?: return null
    if (call.calleeExpression?.text != FiktionOperation.Generates.text) return null
    val target = expression.receiverExpression.takeIf { it.fixedValueGeneratesTargetText() != null } ?: return null
    val value = call.valueArguments.singleOrNull()?.getArgumentExpression() ?: return null
    return FixedProbabilityGeneratesExpression(expression = expression, target = target, valueText = value.text)
}

internal fun KtExpression.fixedProbabilityValue(): Double? =
    numericConstantValue()
        ?: percentProbabilityValue()

private fun KtExpression.percentProbabilityValue(): Double? {
    val expression = this as? KtDotQualifiedExpression ?: return null
    val selector = expression.selectorExpression ?: return null
    if (selector.text != "percent") return null
    return expression.receiverExpression.numericConstantValue()?.div(100.0)
}

internal fun KtExpression.isExplicitlyNullableFixedValueTarget(): Boolean {
    val call =
        when (this) {
            is KtCallExpression -> this
            is KtDotQualifiedExpression -> selectorExpression as? KtCallExpression
            else -> null
        } ?: return false
    val valueTypeIndex =
        when (call.calleeExpression?.text) {
            FiktionCall.Type.text, FiktionCall.Name.text -> 0
            FiktionCall.Property.text -> 1
            else -> return false
        }
    return call.typeArgumentList
        ?.arguments
        ?.getOrNull(valueTypeIndex)
        ?.typeReference
        ?.typeElement is KtNullableType
}

internal const val FIXED_PROBABILITY_DEFAULT_FQ_NAME: String = "dev.s7a.fiktion.default"
internal const val FIXED_PROBABILITY_GENERATES_FQ_NAME: String = "dev.s7a.fiktion.generates"
