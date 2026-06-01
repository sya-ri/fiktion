package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports recursive fake calls inside same-type generators.
 */
public class AvoidRecursiveFakeInGenerator(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid recursive fake calls in same-type generators.",
    ) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.Fake)) return
        val fakeType = expression.singleTypeArgumentText() ?: return
        val targetType = expression.containingTypeGeneratesByTargetType() ?: return
        if (fakeType != targetType) return

        report(
            Finding(
                entity = Entity.from(expression),
                message =
                    "Avoid recursive `fake<$fakeType>()` here. " +
                        "Add an explicit stopping condition or generate a smaller nested value.",
            ),
        )
    }
}

private fun KtCallExpression.singleTypeArgumentText(): String? =
    typeArgumentList
        ?.arguments
        ?.singleOrNull()
        ?.text
        ?.trim()

private fun KtExpression.containingTypeGeneratesByTargetType(): String? =
    containingInfixTypeGeneratesByTargetType() ?: containingRegularCallTypeGeneratesByTargetType()

private fun KtExpression.containingInfixTypeGeneratesByTargetType(): String? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtBinaryExpression>()
        .firstNotNullOfOrNull { expression ->
            if (!expression.usesInfixOperation(FiktionOperation.GeneratesBy)) return@firstNotNullOfOrNull null
            val right = expression.right ?: return@firstNotNullOfOrNull null
            val leftCall = expression.left?.typeTargetCall() ?: return@firstNotNullOfOrNull null
            leftCall.singleTypeArgumentText().takeIf { right.contains(this) }
        }

private fun KtExpression.containingRegularCallTypeGeneratesByTargetType(): String? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtDotQualifiedExpression>()
        .firstNotNullOfOrNull { expression ->
            val selectorCall = expression.selectorExpression as? KtCallExpression ?: return@firstNotNullOfOrNull null
            if (selectorCall.calleeExpression?.text != FiktionOperation.GeneratesBy.text) return@firstNotNullOfOrNull null
            val argument =
                selectorCall.lambdaArguments.singleOrNull()?.getLambdaExpression()
                    ?: selectorCall.valueArguments.singleOrNull()?.getArgumentExpression()
            val receiverCall = expression.receiverExpression.typeTargetCall() ?: return@firstNotNullOfOrNull null
            receiverCall.singleTypeArgumentText().takeIf { argument?.contains(this) == true }
        }

private fun KtExpression.typeTargetCall(): KtCallExpression? =
    when (this) {
        is KtCallExpression -> takeIf { hasCallee(FiktionCall.Type) }
        is KtDotQualifiedExpression -> (selectorExpression as? KtCallExpression)?.takeIf { it.hasCallee(FiktionCall.Type) }
        else -> null
    }
