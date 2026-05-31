package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * Reports container-part targets selected from star-projected rule targets.
 */
public class AvoidContainerPartOnStarProjectedUnknown(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid element/key/value targets on star-projected container rule targets.",
    ) {
    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        val selector = expression.selectorExpression as? KtNameReferenceExpression ?: return
        val partName = selector.getReferencedName()
        if (partName !in CONTAINER_PART_TARGETS) return
        if (!expression.receiverExpression.text.hasStarProjectedRuleTarget()) return

        report(
            Finding(
                entity = Entity.from(selector),
                message = "Avoid `$partName` on a star-projected container target; use a target with concrete type arguments.",
            ),
        )
    }
}

private fun String.hasStarProjectedRuleTarget(): Boolean =
    contains("*") &&
        (
            contains("type<") ||
                contains("property<") ||
                contains("name<")
        )

private val CONTAINER_PART_TARGETS = setOf("element", "key", "value")
