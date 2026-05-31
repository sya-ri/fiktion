package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Reports oneOf generators that can be written with generatesOneOf.
 */
public class PreferGeneratesOneOf(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer generatesOneOf for Fiktion oneOf generators.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val requiredImports = mutableSetOf<String>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
        requiredImports.clear()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.usesInfixOperation(FiktionOperation.GeneratesBy)) return
        val target = expression.left?.fixedValueGeneratesTargetText() ?: return
        val values = (expression.right as? KtLambdaExpression)?.singleOneOfValuesText() ?: return
        reportReplacement(expression = expression, target = target, values = values)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != FiktionOperation.GeneratesBy.text) return
        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return
        val target = qualified.receiverExpression.fixedValueGeneratesTargetText() ?: return
        val values =
            expression.lambdaArguments
                .singleOrNull()
                ?.getLambdaExpression()
                ?.singleOneOfValuesText()
                ?: return
        reportReplacement(expression = qualified, target = target, values = values)
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }

    private fun reportReplacement(
        expression: KtExpression,
        target: String,
        values: String,
    ) {
        val replacement = "$target generatesOneOf $values"
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
            requiredImports += FIKTION_GENERATES_ONE_OF_FQ_NAME
        }
    }
}

private fun KtLambdaExpression.singleOneOfValuesText(): String? =
    bodyExpression
        ?.statements
        ?.singleOrNull()
        ?.oneOfValuesText()

private fun KtExpression.oneOfValuesText(): String? {
    val call = this as? KtCallExpression ?: return null
    if (call.calleeExpression?.text != "oneOf") return null
    val values = call.valueArguments.mapNotNull { argument -> argument.getArgumentExpression() }
    if (values.size != call.valueArguments.size || values.isEmpty()) return null
    if (values.size > 1) return "listOf(${values.joinToString(", ") { expression -> expression.text }})"
    return null
}

private const val FIKTION_GENERATES_ONE_OF_FQ_NAME = "dev.s7a.fiktion.generatesOneOf"
