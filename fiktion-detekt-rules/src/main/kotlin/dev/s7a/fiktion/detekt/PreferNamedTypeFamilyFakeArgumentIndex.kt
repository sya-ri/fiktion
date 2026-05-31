package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports type-family fake helper calls that pass argumentIndex positionally.
 */
public class PreferNamedTypeFamilyFakeArgumentIndex(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer named `argumentIndex = ...` when passing type-family fake argument indexes.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isInsideTypeFamilyGeneratesBy()) return
        if (!expression.hasCallee(TYPE_FAMILY_FAKE_HELPER_CALLS)) return
        val argument = expression.valueArguments.getOrNull(ARGUMENT_INDEX_POSITION)?.takeIf { value -> !value.isNamed() } ?: return
        val argumentText = argument.getArgumentExpression()?.text ?: return
        val replacement = "${FiktionParameter.ArgumentIndex.text} = $argumentText"

        report(
            Finding(
                entity = Entity.from(argument),
                message = "Use `$replacement` instead of positional type-family fake argument index `$argumentText`.",
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

private const val ARGUMENT_INDEX_POSITION = 1
