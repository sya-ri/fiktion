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
 * Reports range generators that can be written with generatesIn.
 */
public class PreferGeneratesInForRange(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer generatesIn for Fiktion range generators.",
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
        val range = (expression.right as? KtLambdaExpression)?.singleRangeGeneratorText() ?: return
        reportReplacement(expression = expression, target = target, range = range)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != FiktionOperation.GeneratesBy.text) return
        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return
        val target = qualified.receiverExpression.fixedValueGeneratesTargetText() ?: return
        val range =
            expression.lambdaArguments
                .singleOrNull()
                ?.getLambdaExpression()
                ?.singleRangeGeneratorText()
                ?: return
        reportReplacement(expression = qualified, target = target, range = range)
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }

    private fun reportReplacement(
        expression: KtExpression,
        target: String,
        range: String,
    ) {
        val replacement = "$target generatesIn $range"
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
            requiredImports += FIKTION_GENERATES_IN_FQ_NAME
        }
    }
}

private fun KtLambdaExpression.singleRangeGeneratorText(): String? =
    bodyExpression
        ?.statements
        ?.singleOrNull()
        ?.rangeGeneratorText()

private fun KtExpression.rangeGeneratorText(): String? {
    val call = this as? KtCallExpression ?: return null
    val generatorName = call.calleeExpression?.text
    if (generatorName !in RANGE_GENERATOR_CALLS) return null
    val argument =
        call.valueArguments
            .singleOrNull()
            ?.getArgumentExpression()
            ?: return null
    if (generatorName == "char" && !argument.isRangeExpression()) return null
    return argument.text
}

private fun KtExpression.isRangeExpression(): Boolean = this is KtBinaryExpression && operationReference.text in RANGE_OPERATORS

private val RANGE_GENERATOR_CALLS =
    setOf("byte", "char", "double", "float", "int", "long", "short", "ubyte", "uint", "ulong", "ushort")

private val RANGE_OPERATORS = setOf("..", "..<")

private const val FIKTION_GENERATES_IN_FQ_NAME = "dev.s7a.fiktion.generatesIn"
