package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports bare using(...) calls that should use explicit receiver infix syntax.
 */
public class PreferThisUsingInFakeSpec(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer `this using ...` over bare `using(...)` in Fiktion spec blocks.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != FiktionOperation.Using.text) return
        if ((expression.parent as? KtDotQualifiedExpression)?.selectorExpression == expression) return
        val argument = expression.valueArguments.singleOrNull()?.getArgumentExpression() ?: return
        val replacement = "this using ${argument.text}"

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
