package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Reports repeated type-argument fake calls in type-family loops.
 */
public class PreferIndexedFakeInTypeFamilyLoop(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer indexed fake calls in type-family loops.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val indexedLoopCalls: Set<String> by config(DEFAULT_INDEXED_LOOP_CALLS.toList()) { calls -> calls.toSet() }

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.Fake)) return
        if (!expression.isInsideTypeFamilyGeneratesBy()) return

        val loopIndex = expression.containingLoopIndexName(indexedLoopCalls) ?: return
        val argumentIndex = expression.singleIntegerLiteralArgumentText() ?: return
        val replacement = "${expression.calleeExpression?.text ?: return}($loopIndex, argumentIndex = $argumentIndex)"

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}` to vary fake index in the loop.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = expression.textRange.startOffset,
                    endOffset = expression.textRange.endOffset,
                    text = replacement,
                )
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}

private fun KtCallExpression.containingLoopIndexName(indexedLoopCalls: Set<String>): String? {
    val generator = containingOperationArgument(FiktionOperation.GeneratesBy) ?: return null
    return generateSequence(getStrictParentOfType<KtLambdaExpression>()) { lambda ->
        lambda.getStrictParentOfType<KtLambdaExpression>()
    }.firstNotNullOfOrNull { lambda ->
        if (lambda == generator || !generator.contains(lambda) || !lambda.isIndexedLoopLambda(indexedLoopCalls)) {
            return@firstNotNullOfOrNull null
        }

        val parameters = lambda.functionLiteral.valueParameters
        when (parameters.size) {
            0 -> IMPLICIT_LOOP_PARAMETER_NAME
            1 -> parameters.single().name
            else -> null
        }
    }
}

private const val IMPLICIT_LOOP_PARAMETER_NAME = "it"
