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

        expression.infixGeneratesByRangeReplacement()?.let { replacement ->
            reportReplacement(replacement)
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        expression.callGeneratesByRangeReplacement()?.let { replacement ->
            reportReplacement(replacement)
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }

    private fun reportReplacement(replacement: GeneratesInReplacement) {
        val replacementText = "${replacement.target} generatesIn ${replacement.range}"
        report(
            Finding(
                entity = Entity.from(replacement.expression),
                message = "Use `$replacementText` instead of `${replacement.expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    replacement.expression.textRange.startOffset,
                    replacement.expression.textRange.endOffset,
                    replacementText,
                )
            requiredImports += FIKTION_GENERATES_IN_FQ_NAME
        }
    }
}

private data class GeneratesInReplacement(
    val expression: KtExpression,
    val target: String,
    val range: String,
)

private fun KtBinaryExpression.infixGeneratesByRangeReplacement(): GeneratesInReplacement? {
    if (!usesInfixOperation(FiktionOperation.GeneratesBy)) return null
    val target = left?.fixedValueGeneratesTargetText() ?: return null
    val range = (right as? KtLambdaExpression)?.singleRangeGeneratorText() ?: return null
    return GeneratesInReplacement(expression = this, target = target, range = range)
}

private fun KtCallExpression.callGeneratesByRangeReplacement(): GeneratesInReplacement? {
    if (calleeExpression?.text != FiktionOperation.GeneratesBy.text) return null
    val qualified = parent as? KtDotQualifiedExpression ?: return null
    if (qualified.selectorExpression != this) return null
    val target = qualified.receiverExpression.fixedValueGeneratesTargetText() ?: return null
    val range =
        lambdaArguments
            .singleOrNull()
            ?.getLambdaExpression()
            ?.singleRangeGeneratorText()
            ?: return null
    return GeneratesInReplacement(expression = qualified, target = target, range = range)
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
