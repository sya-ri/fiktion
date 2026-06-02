package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports fixed default probabilities that can be written as fixed generated values.
 */
public class PreferFixedDefaultProbability(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer fixed generated values over fixed default probabilities.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val requiredImports = mutableSetOf<String>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
        requiredImports.clear()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.usesInfixOperation(FiktionOperation.OrDefaultAt)) return
        val probability = expression.right?.fixedProbabilityValue() ?: return
        val generates = expression.left?.fixedProbabilityGeneratesExpression() ?: return
        val replacement =
            when (probability) {
                0.0 -> {
                    generates.expression.text
                }

                1.0 -> {
                    "${generates.target.text} generates default"
                }

                else -> {
                    return
                }
            }

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
            if (replacement.contains(" generates default")) {
                requiredImports += FIXED_PROBABILITY_DEFAULT_FQ_NAME
                requiredImports += FIXED_PROBABILITY_GENERATES_FQ_NAME
            }
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }
}
