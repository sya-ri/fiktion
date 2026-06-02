package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * Reports Fiktion generators that can be written as fixed values.
 */
public class PreferGeneratesForFixedValue(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer fixed-value Fiktion rules over generators that always produce a fixed value.",
    ) {
    private val constantGenerator: Boolean by config(true)
    private val singleOneOf: Boolean by config(true)
    private val singleGeneratesOneOf: Boolean by config(true)
    private val includeNull: Boolean by config(true)
    private val replacements = mutableListOf<TextReplacement>()
    private val requiredImports = mutableSetOf<String>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
        requiredImports.clear()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val target = expression.left?.fixedValueGeneratesTargetText() ?: return
        val value =
            when {
                expression.usesInfixOperation(FiktionOperation.GeneratesBy) -> {
                    (expression.right as? KtLambdaExpression)?.fixedGeneratorValueText(
                        constantGenerator = constantGenerator,
                        singleOneOf = singleOneOf,
                        includeNull = includeNull,
                    )
                }

                expression.usesInfixOperation(FiktionOperation.GeneratesOneOf) -> {
                    expression.right?.takeIf { singleGeneratesOneOf }?.singleGeneratedValueText(includeNull)
                }

                else -> {
                    null
                }
            } ?: return
        reportReplacement(expression = expression, target = target, value = value)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return
        val target = qualified.receiverExpression.fixedValueGeneratesTargetText() ?: return
        val value =
            when (expression.calleeExpression?.text) {
                FiktionOperation.GeneratesBy.text -> {
                    expression.lambdaArguments
                        .singleOrNull()
                        ?.getLambdaExpression()
                        ?.fixedGeneratorValueText(
                            constantGenerator = constantGenerator,
                            singleOneOf = singleOneOf,
                            includeNull = includeNull,
                        )
                }

                FiktionOperation.GeneratesOneOf.text -> {
                    expression.valueArguments
                        .singleOrNull()
                        ?.getArgumentExpression()
                        ?.takeIf { singleGeneratesOneOf }
                        ?.singleGeneratedValueText(includeNull)
                }

                else -> {
                    null
                }
            }
                ?: return
        reportReplacement(expression = qualified, target = target, value = value)
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }

    private fun reportReplacement(
        expression: KtExpression,
        target: String,
        value: String,
    ) {
        val replacement = "$target generates $value"
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
            requiredImports += FIKTION_GENERATES_FQ_NAME
        }
    }
}

private fun KtLambdaExpression.fixedGeneratorValueText(
    constantGenerator: Boolean,
    singleOneOf: Boolean,
    includeNull: Boolean,
): String? {
    val statement =
        bodyExpression
            ?.statements
            ?.singleOrNull()
            ?: return null
    return statement.fixedGeneratorValueText(
        constantGenerator = constantGenerator,
        singleOneOf = singleOneOf,
        includeNull = includeNull,
    )
}

private fun KtExpression.fixedGeneratorValueText(
    constantGenerator: Boolean,
    singleOneOf: Boolean,
    includeNull: Boolean,
): String? =
    when {
        constantGenerator -> fixedValueText(includeNull)
        else -> null
    }
        ?: takeIf { singleOneOf }?.singleOneOfFixedValueText(includeNull)

private fun KtExpression.singleOneOfFixedValueText(includeNull: Boolean): String? {
    val call = this as? KtCallExpression ?: return null
    if (call.calleeExpression?.text != "oneOf") return null
    return call.valueArguments
        .singleOrNull()
        ?.getArgumentExpression()
        ?.fixedValueText(includeNull)
}

private fun KtExpression.singleGeneratedValueText(includeNull: Boolean): String? =
    fixedValueText(includeNull) ?: singleValueIterableText(includeNull)

private fun KtExpression.singleValueIterableText(includeNull: Boolean): String? {
    val call = this as? KtCallExpression ?: return null
    if (call.calleeExpression?.text !in SINGLE_VALUE_ITERABLE_FACTORY_CALLS) return null
    return call.valueArguments
        .singleOrNull()
        ?.getArgumentExpression()
        ?.fixedValueText(includeNull)
}

private fun KtExpression.fixedValueText(includeNull: Boolean): String? =
    when (this) {
        is KtConstantExpression -> text.takeIf { includeNull || it != "null" }
        is KtStringTemplateExpression -> text.takeIf { entries.all { entry -> entry is KtLiteralStringTemplateEntry } }
        is KtPrefixExpression -> text.takeIf { operationReference.text in setOf("+", "-") && baseExpression is KtConstantExpression }
        is KtParenthesizedExpression -> expression?.fixedValueText(includeNull)?.let { "($it)" }
        else -> null
    }

private val SINGLE_VALUE_ITERABLE_FACTORY_CALLS = setOf("listOf", "setOf")

private const val FIKTION_GENERATES_FQ_NAME = "dev.s7a.fiktion.generates"
