package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Reports `fake(index)` calls whose argument index is outside the requested type-family arguments.
 */
public class AvoidInvalidTypeFamilyFakeIndex(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid invalid fake indexes in type-family generators.",
    ) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.Fake)) return
        val argumentIndex = expression.singleIntegerLiteralArgumentValue() ?: return
        val typeArgumentCount = expression.containingTypeFamilyTypeArgumentCount() ?: return
        if (argumentIndex in 0 until typeArgumentCount) return

        val allowed = allowedIndexDescription(typeArgumentCount)
        report(
            Finding(
                entity = Entity.from(expression),
                message = "`fake($argumentIndex)` is outside the requested type arguments. $allowed.",
            ),
        )
    }
}

private fun allowedIndexDescription(typeArgumentCount: Int): String = "Allowed indexes are 0..${typeArgumentCount - 1}"
