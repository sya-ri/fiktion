package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports oneOf calls that cannot select a value.
 */
public class AvoidEmptyOneOf(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid empty oneOf value sets in Fiktion generators.",
    ) {
    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.usesInfixOperation(FiktionOperation.GeneratesOneOf)) return
        val right = expression.right ?: return
        if (!right.isEmptyValueSetExpression()) return

        reportEmpty(expression)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text !in ONE_OF_CALLS) return
        if (!expression.hasEmptyValueSet()) return

        reportEmpty(expression)
    }

    private fun reportEmpty(expression: KtExpression) {
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Do not use `${expression.text}` because it cannot select a value.",
            ),
        )
    }
}

private fun KtCallExpression.hasEmptyValueSet(): Boolean {
    if (valueArguments.isEmpty()) return true
    return valueArguments.singleOrNull()?.getArgumentExpression()?.isEmptyValueSetExpression() == true
}

private fun KtExpression.isEmptyValueSetExpression(): Boolean {
    val call = this as? KtCallExpression ?: return false
    return call.calleeExpression?.text in EMPTY_COLLECTION_CALLS && call.valueArguments.isEmpty()
}

private val ONE_OF_CALLS = setOf("oneOf", FiktionOperation.GeneratesOneOf.text)
private val EMPTY_COLLECTION_CALLS = setOf("emptyList", "emptySet", "listOf", "setOf")
