package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports fake calls that pass seed positionally.
 */
public class PreferExplicitFakeSeedName(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer explicit `seed = ...` when passing fake seeds.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.Fake)) return
        if (expression.typeArguments.isEmpty()) return
        if (expression.hasArgumentNamed(FiktionParameter.Seed)) return
        val argument = expression.valueArguments.firstOrNull()?.takeIf { value -> !value.isNamed() } ?: return
        val seedText = argument.getArgumentExpression()?.text ?: return
        val replacement = "seed = $seedText"

        report(
            Finding(
                entity = Entity.from(argument),
                message = "Use `$replacement` instead of positional fake seed `$seedText`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(argument.textRange.startOffset, argument.textRange.endOffset, replacement)
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}
