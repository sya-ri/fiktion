package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports adjacent rules that target the same Fiktion rule target.
 */
public class PreferGroupedRuleTarget(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer grouping rules for the same Fiktion rule target.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private var needsInvokeImport = false

    override fun preVisit(root: KtFile) {
        replacements.clear()
        needsInvokeImport = false
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return

        expression.statements
            .groupAdjacent()
            .filter { group -> group.size > 1 }
            .forEach { group -> reportGroup(group) }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, setOf(FIKTION_INVOKE_FQ_NAME).takeIf { needsInvokeImport }.orEmpty())
        }
    }

    private fun reportGroup(group: List<GroupableRuleStatement>) {
        val first = group.first()
        report(
            Finding(
                entity = Entity.from(first.expression),
                message = "Group rules for `${first.targetText}` into a single target block.",
            ),
        )
        if (autoCorrect) {
            replacements += group.replacement() ?: return
            needsInvokeImport = needsInvokeImport || group.any { statement -> statement.requiresInvokeImport }
        }
    }
}

private data class GroupableRuleStatement(
    val expression: KtBinaryExpression,
    val targetText: String,
    val blockTargetText: String,
    val operation: FiktionOperation,
    val argumentText: String,
    val requiresInvokeImport: Boolean,
)

private fun KtExpression.groupableRuleStatement(): GroupableRuleStatement? {
    val expression = this as? KtBinaryExpression ?: return null
    val operation = GROUPABLE_RULE_OPERATIONS.firstOrNull { operation -> expression.usesInfixOperation(operation) } ?: return null
    val left = expression.left ?: return null
    val right = expression.right ?: return null
    val target = left.fiktionRuleTargetText(GROUPABLE_RULE_TARGET_CALLS) ?: return null
    return GroupableRuleStatement(
        expression = expression,
        targetText = target.sourceText,
        blockTargetText = target.blockText,
        operation = operation,
        argumentText = right.text,
        requiresInvokeImport = target.requiresInvokeImport,
    )
}

private fun List<KtExpression>.groupAdjacent(): List<List<GroupableRuleStatement>> {
    val groups = mutableListOf<List<GroupableRuleStatement>>()
    var current = mutableListOf<GroupableRuleStatement>()
    for (expression in this) {
        val statement = expression.groupableRuleStatement()
        if (statement == null) {
            if (current.isNotEmpty()) groups += current
            current = mutableListOf()
            continue
        }
        val previous = current.lastOrNull()
        if (previous != null &&
            previous.targetText == statement.targetText &&
            previous.blockTargetText == statement.blockTargetText &&
            previous.expression.textBetween(statement.expression).isBlank() &&
            previous.expression.endLineNumber() + 1 == statement.expression.startLineNumber()
        ) {
            current += statement
        } else {
            if (current.isNotEmpty()) groups += current
            current = mutableListOf(statement)
        }
    }
    if (current.isNotEmpty()) groups += current
    return groups
}

private fun List<GroupableRuleStatement>.replacement(): TextReplacement? {
    val first = first()
    val last = last()
    val indent = first.expression.lineIndent()
    val innerIndent = "$indent    "
    return TextReplacement(
        startOffset = first.expression.textRange.startOffset,
        endOffset = last.expression.textRange.endOffset,
        text =
            buildString {
                append(first.blockTargetText)
                appendLine(" {")
                this@replacement.forEach { statement ->
                    append(innerIndent)
                    append("this ")
                    append(statement.operation.text)
                    append(" ")
                    appendLine(statement.argumentText)
                }
                append(indent)
                append("}")
            },
    )
}

private fun KtExpression.textBetween(other: KtExpression): String =
    containingKtFile.text.substring(textRange.endOffset, other.textRange.startOffset)

private fun KtExpression.startLineNumber(): Int = containingKtFile.text.take(textRange.startOffset).count { character -> character == '\n' }

private fun KtExpression.endLineNumber(): Int = containingKtFile.text.take(textRange.endOffset).count { character -> character == '\n' }

private val GROUPABLE_RULE_TARGET_CALLS = setOf(FiktionCall.Type, FiktionCall.Property, FiktionCall.Name)

private val GROUPABLE_RULE_OPERATIONS = FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using

private const val FIKTION_INVOKE_FQ_NAME = "dev.s7a.fiktion.invoke"
