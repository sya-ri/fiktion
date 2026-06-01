package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports fixed Fiktion config ranges that can be written as fixed values.
 */
public class PreferFixedConfigValue(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer fixed-value Fiktion config calls over equal fixed ranges.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isFiktionConfigCall()) return

        val argument = expression.valueArguments.singleOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return
        if (argument.operationReference.text != RANGE_TO_OPERATION) return

        val left = argument.left as? KtConstantExpression ?: return
        val right = argument.right as? KtConstantExpression ?: return
        if (left.text != right.text) return

        report(
            Finding(
                entity = Entity.from(argument),
                message = "Use `${expression.renderCall(left.text)}` instead of `${expression.renderCall(argument.text)}`.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = argument.textRange.startOffset,
                    endOffset = argument.textRange.endOffset,
                    text = left.text,
                )
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect && replacements.isNotEmpty()) {
            root.applyReplacements(replacements)
        }
    }
}

private const val RANGE_TO_OPERATION = ".."
