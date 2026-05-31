package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtThisExpression

/**
 * Reports grouped target blocks that declare generators before configs.
 */
public class PreferRuleTargetDeclarationOrder(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer declaring Fiktion configs before generators in grouped target blocks.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return

        val declarations = expression.statements.map { statement -> statement.groupedTargetDeclaration() }
        val firstMisorderedConfig =
            declarations
                .filterNotNull()
                .firstOrNull { declaration ->
                    declaration.operation == FiktionOperation.Using &&
                        declarations.hasGeneratorBefore(declaration)
                }
                ?: return

        report(
            Finding(
                entity = Entity.from(firstMisorderedConfig.expression),
                message = "Declare configs before generators in this Fiktion target block.",
            ),
        )
        if (autoCorrect && declarations.all { declaration -> declaration != null }) {
            replacements += expression.reorderedReplacement(declarations.filterNotNull())
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}

private data class GroupedTargetDeclaration(
    val expression: KtExpression,
    val operation: FiktionOperation,
)

private fun KtExpression.groupedTargetDeclaration(): GroupedTargetDeclaration? {
    val expression = this as? KtBinaryExpression ?: return null
    val left = expression.left as? KtThisExpression ?: return null
    if (left.text != "this") return null
    val operation =
        GROUPED_TARGET_DECLARATION_OPERATIONS
            .firstOrNull { operation ->
                expression.usesInfixOperation(operation)
            } ?: return null
    return GroupedTargetDeclaration(expression = expression, operation = operation)
}

private fun List<GroupedTargetDeclaration?>.hasGeneratorBefore(declaration: GroupedTargetDeclaration): Boolean =
    takeWhile { candidate -> candidate != declaration }
        .filterNotNull()
        .any { candidate -> candidate.operation in FIKTION_GENERATOR_OPERATIONS }

private fun KtBlockExpression.reorderedReplacement(declarations: List<GroupedTargetDeclaration>): TextReplacement {
    val configs = declarations.filter { declaration -> declaration.operation == FiktionOperation.Using }
    val generators = declarations.filter { declaration -> declaration.operation in FIKTION_GENERATOR_OPERATIONS }
    val first = declarations.first().expression
    val last = declarations.last().expression
    val indent = first.lineIndent()
    return TextReplacement(
        startOffset = first.textRange.startOffset,
        endOffset = last.textRange.endOffset,
        text =
            (configs + generators)
                .map { declaration -> declaration.expression.text.trimStart() }
                .joinToString(separator = "\n$indent"),
    )
}

private val GROUPED_TARGET_DECLARATION_OPERATIONS = FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using
