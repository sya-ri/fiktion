package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports dot-call Fiktion DSL operations that can be written as infix declarations.
 */
public class PreferInfixFiktionDsl(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer Fiktion infix DSL declarations over dot-call syntax.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        val selector = expression.selectorExpression as? KtCallExpression ?: return
        val operation = INFIX_DSL_OPERATIONS.firstOrNull { operation -> selector.calleeExpression?.text == operation.text } ?: return
        if (!expression.receiverExpression.supportsInfixOperation(operation)) return
        val argument = selector.singleInfixArgumentText() ?: return
        val replacement = "${expression.receiverExpression.text} ${operation.text} $argument"

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}

private fun KtExpression.supportsInfixOperation(operation: FiktionOperation): Boolean {
    val calls =
        when (operation) {
            FiktionOperation.Using -> FIKTION_CONFIG_RULE_TARGET_CALLS
            else -> FIKTION_GENERATOR_RULE_TARGET_CALLS
        }
    return fiktionRuleTargetText(calls = calls, allowCallableReference = true, allowThis = true) != null
}

private fun KtCallExpression.singleInfixArgumentText(): String? =
    lambdaArguments.singleOrNull()?.getLambdaExpression()?.text
        ?: valueArguments.singleOrNull()?.getArgumentExpression()?.text

private val INFIX_DSL_OPERATIONS = FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using
