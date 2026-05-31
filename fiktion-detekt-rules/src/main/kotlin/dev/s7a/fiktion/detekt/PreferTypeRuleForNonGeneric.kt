package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports non-generic type-family targets that can be written as exact type targets.
 */
public class PreferTypeRuleForNonGeneric(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer exact Fiktion type rules over non-generic type-family rules.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val requiredImports = mutableSetOf<String>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
        requiredImports.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.TypeFamily)) return
        val typeArgumentList = expression.typeArgumentList ?: return
        val typeArgument = typeArgumentList.arguments.singleOrNull()?.text ?: return
        if (typeArgument.contains("<") || typeArgument.contains("*")) return
        if (expression.usesTypeFamilyFakeHelpers()) return

        val replacement = expression.text.replaceFirst(FiktionCall.TypeFamily.text, FiktionCall.Type.text)
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = expression.textRange.startOffset,
                    endOffset = expression.textRange.endOffset,
                    text = replacement,
                )
            if (!expression.isSelectorOfDotQualifiedExpression()) {
                requiredImports += FIKTION_TYPE_FQ_NAME
            }
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }
}

private fun KtCallExpression.usesTypeFamilyFakeHelpers(): Boolean =
    containingOperationArgument(FiktionOperation.GeneratesBy)?.hasDescendantCall(TYPE_FAMILY_FAKE_HELPER_CALLS) == true

private fun KtCallExpression.isSelectorOfDotQualifiedExpression(): Boolean =
    (parent as? KtDotQualifiedExpression)?.selectorExpression == this

private const val FIKTION_TYPE_FQ_NAME = "dev.s7a.fiktion.type"
