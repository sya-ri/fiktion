package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * Reports stored Fiktion.configure snapshots that are not restored in the same block.
 */
public class AvoidGlobalFiktionConfigureWithoutRestore(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid global Fiktion.configure snapshots without restore calls.",
    ) {
    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        val name = property.name ?: return
        val initializer = property.initializer as? KtDotQualifiedExpression ?: return
        val configureCall = initializer.selectorExpression as? KtCallExpression ?: return
        if (!configureCall.isFiktionConfigureCall()) return

        val block = property.parent as? KtBlockExpression ?: return
        val propertyIndex = block.statements.indexOf(property).takeIf { index -> index >= 0 } ?: return
        val isRestored =
            block.statements
                .drop(propertyIndex + 1)
                .any { statement -> statement.hasRestoreCallFor(name) }
        if (isRestored) return

        report(
            Finding(
                entity = Entity.from(property),
                message = "Restore the Fiktion.configure snapshot `$name` in the same block.",
            ),
        )
    }
}

private fun KtDotQualifiedExpression.isRestoreCallFor(name: String): Boolean {
    val selectorCall = selectorExpression as? KtCallExpression ?: return false
    return receiverExpression.text == name && selectorCall.calleeExpression?.text == "restore"
}

private fun KtElement.hasRestoreCallFor(name: String): Boolean =
    (this is KtDotQualifiedExpression && isRestoreCallFor(name)) ||
        anyDescendantOfType<KtDotQualifiedExpression> { expression -> expression.isRestoreCallFor(name) }
