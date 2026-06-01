package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

/**
 * Reports discarded Fiktion.configure snapshots.
 */
public class AvoidUnusedFiktionSnapshot(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid discarding Fiktion.configure snapshots.",
    ) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != "configure") return
        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return
        if (qualified.receiverExpression.text != "Fiktion") return
        val block = qualified.parent as? KtBlockExpression ?: return
        if (qualified !in block.statements) return

        report(
            Finding(
                entity = Entity.from(qualified),
                message = "Store the Fiktion.configure snapshot and restore it after the test scope.",
            ),
        )
    }
}
