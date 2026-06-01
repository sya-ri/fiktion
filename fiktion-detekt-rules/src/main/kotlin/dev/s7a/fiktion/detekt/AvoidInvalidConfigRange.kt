package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports statically invalid Fiktion config ranges.
 */
public class AvoidInvalidConfigRange(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid invalid Fiktion config ranges.",
    ) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isFiktionConfigCall()) return
        val keyText = expression.fiktionConfigKeyText() ?: return
        val argument = expression.valueArguments.singleOrNull()?.getArgumentExpression()
        val invalidReason = argument.invalidConfigReason(keyText) ?: return

        report(
            Finding(
                entity = Entity.from(argument ?: expression),
                message = "Avoid invalid Fiktion config `${expression.renderCall(argument?.text.orEmpty())}`: $invalidReason.",
            ),
        )
    }
}

private fun KtExpression?.invalidConfigReason(keyText: String): String? {
    val expression = this ?: return null
    val range = expression as? KtBinaryExpression
    if (range != null && range.operationReference.text in RANGE_OPERATIONS) {
        val left = range.left?.numericConstantValue() ?: return null
        val right = range.right?.numericConstantValue() ?: return null
        val empty = if (range.operationReference.text == RANGE_UNTIL_OPERATION) left >= right else left > right
        if (empty) return "range must not be empty"
        if (keyText.hasNonNegativeConfigKey() && left < 0.0) return "range lower bound must not be negative"
        return null
    }

    val value = expression.numericConstantValue() ?: return null
    if (keyText.hasNonNegativeConfigKey() && value < 0.0) return "value must not be negative"
    if (keyText.hasPositiveConfigKey() && value <= 0.0) return "value must be positive"
    return null
}

private fun String.hasNonNegativeConfigKey(): Boolean =
    endsWith(".length") ||
        endsWith(".size") ||
        endsWith(".nanosecond") ||
        endsWith(".nanoseconds") ||
        endsWith(".nanosecondsOfDay") ||
        endsWith(".epochDays")

private fun String.hasPositiveConfigKey(): Boolean = endsWith(".step")

private val RANGE_OPERATIONS = setOf("..", RANGE_UNTIL_OPERATION)
private const val RANGE_UNTIL_OPERATION = "..<"
