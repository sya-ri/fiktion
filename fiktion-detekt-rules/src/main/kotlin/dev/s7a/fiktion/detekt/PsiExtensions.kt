package dev.s7a.fiktion.detekt

import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Fiktion DSL operations used by detekt rules.
 */
internal enum class FiktionOperation(
    val text: String,
) {
    Generates("generates"),
    GeneratesBy("generatesBy"),
    GeneratesIn("generatesIn"),
    GeneratesOneOf("generatesOneOf"),
    Using("using"),
}

/**
 * Fiktion DSL calls used by detekt rules.
 */
internal enum class FiktionCall(
    val text: String,
) {
    Fake("fake"),
    FakeElement("fakeElement"),
    FakeKey("fakeKey"),
    FakeValue("fakeValue"),
    Name("name"),
    Property("property"),
    Type("type"),
    TypeFamily("typeFamily"),
    WithSeed("withSeed"),
}

/**
 * Fiktion DSL call parameters used by detekt rules.
 */
internal enum class FiktionParameter(
    val text: String,
) {
    ArgumentIndex("argumentIndex"),
    Seed("seed"),
}

/**
 * Source text for a Fiktion rule target.
 */
internal data class FiktionRuleTargetText(
    val sourceText: String,
    val blockText: String,
    val requiresInvokeImport: Boolean,
)

/**
 * Source information for a Fiktion config declaration on a rule target.
 */
internal data class FiktionRuleTargetConfig(
    val expression: KtExpression,
    val targetText: String,
    val configKeyText: String,
)

/**
 * Source information for a Fiktion generator declaration on a rule target.
 */
internal data class FiktionGeneratorRuleTarget(
    val expression: KtExpression,
    val text: String,
)

/**
 * Returns true when this call uses `FiktionConfig` or an import alias for `FiktionConfig` as its receiver.
 */
internal fun KtCallExpression.isFiktionConfigCall(): Boolean {
    val qualified = parent as? KtDotQualifiedExpression ?: return false
    if (qualified.selectorExpression != this) return false
    val receiver = qualified.receiverExpression.text
    return containingKtFile.fiktionConfigNames.any { name -> receiver == name || receiver.startsWith("$name.") }
}

/**
 * Renders this call with [argument] while preserving its current receiver, if any.
 */
internal fun KtCallExpression.renderCall(argument: String): String {
    val qualified = parent as? KtDotQualifiedExpression
    val receiver = qualified?.receiverExpression?.text
    val callee = calleeExpression?.text ?: return text
    return if (receiver == null) {
        "$callee($argument)"
    } else {
        "$receiver.$callee($argument)"
    }
}

/**
 * Returns the source text for this config expression's key without its value argument.
 */
internal fun KtExpression.fiktionConfigKeyText(): String? {
    val call =
        when (this) {
            is KtCallExpression -> this
            is KtDotQualifiedExpression -> selectorExpression as? KtCallExpression
            else -> null
        } ?: return null
    return call.fiktionConfigKeyText()
}

private fun KtCallExpression.fiktionConfigKeyText(): String? {
    if (!isFiktionConfigCall()) return null
    val callee = calleeExpression?.text ?: return null
    val receiver = (parent as? KtDotQualifiedExpression)?.receiverExpression?.text ?: return callee
    return "$receiver.$callee"
}

/**
 * Returns the source text of a receiver that supports fixed-value `generates`, or null otherwise.
 */
internal fun KtExpression.fixedValueGeneratesTargetText(): String? =
    when (this) {
        is KtCallableReferenceExpression -> {
            text
        }

        is KtCallExpression -> {
            text.takeIf { hasCallee(FIXED_VALUE_GENERATES_CALLS) }
        }

        is KtDotQualifiedExpression -> {
            val selectorCall = selectorExpression as? KtCallExpression
            text.takeIf { selectorCall?.hasCallee(FIXED_VALUE_GENERATES_CALLS) == true }
        }

        else -> {
            null
        }
    }

/**
 * Returns true when this call's callee text matches [call].
 */
internal fun KtCallExpression.hasCallee(call: FiktionCall): Boolean = calleeExpression?.text == call.text

/**
 * Returns true when this call's callee text is included in [calls].
 */
internal fun KtCallExpression.hasCallee(calls: Set<FiktionCall>): Boolean = calls.any { call -> hasCallee(call) }

/**
 * Returns true when this call has an argument with [parameter].
 */
internal fun KtCallExpression.hasArgumentNamed(parameter: FiktionParameter): Boolean =
    valueArguments.any { argument -> argument.getArgumentName()?.asName?.asString() == parameter.text }

/**
 * Returns this call's single unsigned integer literal argument text.
 */
internal fun KtCallExpression.singleIntegerLiteralArgumentText(): String? {
    val argument = valueArguments.singleOrNull()?.getArgumentExpression() as? KtConstantExpression ?: return null
    return argument.text.takeIf { text -> text.all(Char::isDigit) }
}

/**
 * Returns this call's single unsigned integer literal argument value.
 */
internal fun KtCallExpression.singleIntegerLiteralArgumentValue(): Int? = singleIntegerLiteralArgumentText()?.toIntOrNull()

/**
 * Returns declarations that repeat a key already seen in this block. Only the first repeat for each key is returned.
 */
internal fun <Declaration, Key> KtBlockExpression.repeatedDeclarations(
    declaration: (KtExpression) -> Declaration?,
    key: (Declaration) -> Key,
): List<Declaration> {
    val seen = mutableSetOf<Key>()
    val reported = mutableSetOf<Key>()
    return statements
        .mapNotNull(declaration)
        .filter { value ->
            val valueKey = key(value)
            !seen.add(valueKey) && reported.add(valueKey)
        }
}

/**
 * Returns true when this expression uses [operation] as its infix operation.
 */
internal fun KtBinaryExpression.usesInfixOperation(operation: FiktionOperation): Boolean = operationReference.text == operation.text

/**
 * Returns true when this expression uses one of [operations] as its infix operation.
 */
internal fun KtBinaryExpression.usesInfixOperation(operations: Set<FiktionOperation>): Boolean =
    operations.any { operation -> usesInfixOperation(operation) }

/**
 * Returns this expression as a Fiktion rule target config declaration, or null otherwise.
 */
internal fun KtExpression.ruleTargetConfigText(): FiktionRuleTargetConfig? =
    infixRuleTargetConfigText() ?: regularCallRuleTargetConfigText()

private fun KtExpression.infixRuleTargetConfigText(): FiktionRuleTargetConfig? {
    val expression = this as? KtBinaryExpression ?: return null
    if (!expression.usesInfixOperation(FiktionOperation.Using)) return null
    val target =
        expression.left?.fiktionRuleTargetText(
            calls = FIKTION_CONFIG_RULE_TARGET_CALLS,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    val configKey = expression.right?.fiktionConfigKeyText() ?: return null
    return FiktionRuleTargetConfig(
        expression = expression,
        targetText = target.sourceText,
        configKeyText = configKey,
    )
}

private fun KtExpression.regularCallRuleTargetConfigText(): FiktionRuleTargetConfig? {
    val expression = this as? KtDotQualifiedExpression ?: return null
    val selectorCall = expression.selectorExpression as? KtCallExpression ?: return null
    if (selectorCall.calleeExpression?.text != FiktionOperation.Using.text) return null
    val target =
        expression.receiverExpression.fiktionRuleTargetText(
            calls = FIKTION_CONFIG_RULE_TARGET_CALLS,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    val configKey =
        selectorCall.valueArguments
            .singleOrNull()
            ?.getArgumentExpression()
            ?.fiktionConfigKeyText()
            ?: return null
    return FiktionRuleTargetConfig(
        expression = expression,
        targetText = target.sourceText,
        configKeyText = configKey,
    )
}

/**
 * Returns this expression as a Fiktion generator declaration on a rule target, or null otherwise.
 */
internal fun KtExpression.generatorRuleTargetText(): FiktionGeneratorRuleTarget? =
    infixGeneratorRuleTargetText() ?: regularCallGeneratorRuleTargetText()

private fun KtExpression.infixGeneratorRuleTargetText(): FiktionGeneratorRuleTarget? {
    val expression = this as? KtBinaryExpression ?: return null
    if (!expression.usesInfixOperation(FIKTION_GENERATOR_OPERATIONS)) return null
    val target =
        expression.left?.fiktionRuleTargetText(
            calls = FIKTION_GENERATOR_RULE_TARGET_CALLS,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    return FiktionGeneratorRuleTarget(expression = expression, text = target.sourceText)
}

private fun KtExpression.regularCallGeneratorRuleTargetText(): FiktionGeneratorRuleTarget? {
    val expression = this as? KtDotQualifiedExpression ?: return null
    val selectorCall = expression.selectorExpression as? KtCallExpression ?: return null
    if (FIKTION_GENERATOR_OPERATIONS.none { operation -> selectorCall.calleeExpression?.text == operation.text }) return null
    val target =
        expression.receiverExpression.fiktionRuleTargetText(
            calls = FIKTION_GENERATOR_RULE_TARGET_CALLS,
            allowCallableReference = true,
            allowThis = true,
        ) ?: return null
    return FiktionGeneratorRuleTarget(expression = expression, text = target.sourceText)
}

/**
 * Returns true when this expression is a `withSeed` statement.
 */
internal fun KtExpression.isWithSeedStatement(): Boolean =
    when (this) {
        is KtBinaryExpression -> {
            operationReference.text == FiktionCall.WithSeed.text || text.startsWith("${FiktionCall.WithSeed.text} ")
        }

        is KtCallExpression -> {
            hasCallee(FiktionCall.WithSeed) || text.startsWith("${FiktionCall.WithSeed.text}(")
        }

        else -> {
            text.startsWith("${FiktionCall.WithSeed.text} ") ||
                text.startsWith("${FiktionCall.WithSeed.text}(") ||
                anyDescendantOfType<KtBinaryExpression> { expression ->
                    expression.operationReference.text == FiktionCall.WithSeed.text
                } ||
                anyDescendantOfType<KtCallExpression> { expression ->
                    expression.hasCallee(FiktionCall.WithSeed) ||
                        expression.text.startsWith("${FiktionCall.WithSeed.text}(")
                }
        }
    }

/**
 * Returns the seed argument from a `withSeed` statement.
 */
internal fun KtExpression.withSeedArgumentText(): String? =
    when (this) {
        is KtBinaryExpression -> {
            right?.text
        }

        is KtCallExpression -> {
            valueArguments.singleOrNull()?.getArgumentExpression()?.text
        }

        else -> {
            text.removePrefix("${FiktionCall.WithSeed.text} ").takeIf { value -> value != text }
        }
    }

/**
 * Returns true when this call is `Fiktion.configure`.
 */
internal fun KtCallExpression.isFiktionConfigureCall(): Boolean {
    if (calleeExpression?.text != FIKTION_CONFIGURE_CALL) return false
    val qualified = parent as? KtDotQualifiedExpression ?: return false
    return qualified.selectorExpression == this && qualified.receiverExpression.text == FIKTION_OBJECT_NAME
}

/**
 * Returns this expression's numeric literal value, including simple unary +/- prefixes.
 */
internal fun KtExpression.numericConstantValue(): Double? =
    when (this) {
        is KtConstantExpression -> {
            text.normalizedNumberText()?.toDoubleOrNull()
        }

        is KtPrefixExpression -> {
            val value = baseExpression?.numericConstantValue() ?: return null
            when (operationReference.text) {
                "-" -> {
                    -value
                }

                "+" -> {
                    value
                }

                else -> {
                    null
                }
            }
        }

        else -> {
            null
        }
    }

private fun String.normalizedNumberText(): String? {
    val text =
        removeSuffix("uL")
            .removeSuffix("UL")
            .removeSuffix("L")
            .removeSuffix("u")
            .removeSuffix("U")
            .removeSuffix("f")
            .removeSuffix("F")
    return text.replace("_", "").takeIf { value -> value.any(Char::isDigit) }
}

/**
 * Returns this expression as a Fiktion rule target, or null when the expression is not an allowed target.
 */
internal fun KtExpression.fiktionRuleTargetText(
    calls: Set<FiktionCall>,
    allowCallableReference: Boolean = false,
    allowThis: Boolean = false,
): FiktionRuleTargetText? =
    when (this) {
        is KtCallableReferenceExpression -> {
            if (allowCallableReference) {
                FiktionRuleTargetText(sourceText = text, blockText = text, requiresInvokeImport = false)
            } else {
                null
            }
        }

        is KtThisExpression -> {
            if (allowThis) {
                FiktionRuleTargetText(sourceText = text, blockText = text, requiresInvokeImport = false)
            } else {
                null
            }
        }

        is KtCallExpression -> {
            callRuleTargetText(calls)
        }

        is KtDotQualifiedExpression -> {
            dotQualifiedRuleTargetText(calls)
        }

        else -> {
            null
        }
    }

private fun KtCallExpression.callRuleTargetText(calls: Set<FiktionCall>): FiktionRuleTargetText? {
    val call = FiktionCall.entries.firstOrNull { call -> hasCallee(call) } ?: return null
    if (call == FiktionCall.Name && typeArguments.isEmpty()) return null
    if (call !in calls) return null
    val blockText =
        when (call) {
            FiktionCall.Name -> "$text.invoke"
            else -> text
        }
    return FiktionRuleTargetText(
        sourceText = text,
        blockText = blockText,
        requiresInvokeImport = call in INVOKE_BASED_RULE_TARGET_CALLS,
    )
}

private fun KtDotQualifiedExpression.dotQualifiedRuleTargetText(calls: Set<FiktionCall>): FiktionRuleTargetText? {
    val selectorCall = selectorExpression as? KtCallExpression ?: return null
    val selectorTarget = selectorCall.callRuleTargetText(calls) ?: return null
    return FiktionRuleTargetText(
        sourceText = text,
        blockText = "${receiverExpression.text}.${selectorTarget.blockText}",
        requiresInvokeImport = selectorTarget.requiresInvokeImport,
    )
}

/**
 * Returns the argument of the nearest containing Fiktion DSL operation.
 *
 * This supports both infix syntax (`type<T>() generatesBy { ... }`) and regular call syntax
 * (`type<T>().generatesBy { ... }`).
 */
internal fun KtExpression.containingOperationArgument(operation: FiktionOperation): KtExpression? =
    containingInfixRightOperand(operation) ?: containingRegularCallArgument(operation)

/**
 * Returns true when this expression is inside the argument of the nearest containing Fiktion DSL operation.
 */
internal fun KtExpression.isInsideOperationArgument(operation: FiktionOperation): Boolean {
    val argument = containingOperationArgument(operation) ?: return false
    return argument.contains(this)
}

/**
 * Returns true when this expression is inside a `typeFamily<...>() generatesBy { ... }` generator body.
 */
internal fun KtExpression.isInsideTypeFamilyGeneratesBy(): Boolean = containingTypeFamilyGeneratesByTarget() != null

/**
 * Returns the nearest `typeFamily<...>()` target for this expression when it is inside `generatesBy`.
 */
internal fun KtExpression.containingTypeFamilyGeneratesByTarget(): KtExpression? =
    containingInfixTypeFamilyGeneratesByTarget() ?: containingRegularCallTypeFamilyGeneratesByTarget()

/**
 * Returns the requested type argument count for the nearest containing `typeFamily<...>() generatesBy`.
 */
internal fun KtExpression.containingTypeFamilyTypeArgumentCount(): Int? =
    containingTypeFamilyGeneratesByTarget()?.typeFamilyCall()?.typeFamilyTypeArgumentCount()

/**
 * Returns true when this expression is the receiver side of a dot-qualified expression.
 */
internal fun KtExpression.isReceiverOfDotQualifiedExpression(): Boolean {
    val qualified = parent as? KtDotQualifiedExpression ?: return false
    return qualified.receiverExpression == this
}

/**
 * Returns true when this expression is inside a language loop or a loop-like lambda call.
 */
internal fun KtExpression.isInsideLoop(loopCalls: Set<String> = DEFAULT_LOOP_CALLS): Boolean =
    generateSequence(parent) { element -> element.parent }
        .any { element ->
            when (element) {
                is KtForExpression, is KtWhileExpression, is KtDoWhileExpression -> true
                is KtLambdaExpression -> element.isLoopLambda(loopCalls)
                else -> false
            }
        }

private fun KtExpression.containingInfixRightOperand(operation: FiktionOperation): KtExpression? =
    getStrictParentOfType<KtBinaryExpression>()
        ?.takeIf { expression -> expression.usesInfixOperation(operation) }
        ?.right

private fun KtExpression.containingRegularCallArgument(operation: FiktionOperation): KtExpression? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtDotQualifiedExpression>()
        .firstNotNullOfOrNull { expression ->
            val selectorCall = expression.selectorExpression as? KtCallExpression ?: return@firstNotNullOfOrNull null
            if (selectorCall.calleeExpression?.text != operation.text) return@firstNotNullOfOrNull null
            val argument =
                selectorCall.lambdaArguments.singleOrNull()?.getLambdaExpression()
                    ?: selectorCall.valueArguments.singleOrNull()?.getArgumentExpression()
            if (expression.receiverExpression.contains(this) || argument?.contains(this) == true) {
                argument
            } else {
                null
            }
        }

private fun KtExpression.containingInfixTypeFamilyGeneratesByTarget(): KtExpression? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtBinaryExpression>()
        .firstNotNullOfOrNull { expression ->
            if (!expression.usesInfixOperation(FiktionOperation.GeneratesBy)) return@firstNotNullOfOrNull null
            val right = expression.right ?: return@firstNotNullOfOrNull null
            val left = expression.left ?: return@firstNotNullOfOrNull null
            left.takeIf { right.contains(this) && it.isTypeFamilyTarget() }
        }

private fun KtExpression.containingRegularCallTypeFamilyGeneratesByTarget(): KtExpression? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtDotQualifiedExpression>()
        .firstNotNullOfOrNull { expression ->
            val selectorCall = expression.selectorExpression as? KtCallExpression ?: return@firstNotNullOfOrNull null
            if (selectorCall.calleeExpression?.text != FiktionOperation.GeneratesBy.text) return@firstNotNullOfOrNull null
            val argument =
                selectorCall.lambdaArguments.singleOrNull()?.getLambdaExpression()
                    ?: selectorCall.valueArguments.singleOrNull()?.getArgumentExpression()
            expression.receiverExpression.takeIf { argument?.contains(this) == true && it.isTypeFamilyTarget() }
        }

private fun KtExpression.isTypeFamilyTarget(): Boolean = typeFamilyCall() != null

/**
 * Returns the `typeFamily` call represented by this expression.
 */
internal fun KtExpression.typeFamilyCall(): KtCallExpression? =
    when (this) {
        is KtCallExpression -> takeIf { hasCallee(FiktionCall.TypeFamily) }
        is KtDotQualifiedExpression -> (selectorExpression as? KtCallExpression)?.takeIf { it.hasCallee(FiktionCall.TypeFamily) }
        else -> null
    }

/**
 * Returns true when this expression has a descendant Fiktion DSL call included in [calls].
 */
internal fun KtExpression.hasDescendantCall(calls: Set<FiktionCall>): Boolean =
    anyDescendantOfType<KtCallExpression> { call ->
        call.hasCallee(calls)
    }

/**
 * Calls available in `TypeFamilyGenerationContext` that generate values from requested type arguments.
 */
internal val TYPE_FAMILY_FAKE_HELPER_CALLS =
    setOf(FiktionCall.Fake, FiktionCall.FakeElement, FiktionCall.FakeKey, FiktionCall.FakeValue)

internal fun KtExpression.contains(expression: KtExpression): Boolean =
    textRange.startOffset <= expression.textRange.startOffset && expression.textRange.endOffset <= textRange.endOffset

internal fun KtExpression.lineIndent(): String {
    val fileText = containingKtFile.text
    val lineStart = fileText.lastIndexOf('\n', textRange.startOffset - 1).let { index -> if (index == -1) 0 else index + 1 }
    return fileText.substring(lineStart, textRange.startOffset).takeWhile(Char::isWhitespace)
}

/**
 * Returns true when this lambda belongs to a standard indexed loop call such as `List(size) { index -> ... }`.
 */
internal fun KtLambdaExpression.isIndexedLoopLambda(indexedLoopCalls: Set<String> = DEFAULT_INDEXED_LOOP_CALLS): Boolean =
    containingCallExpression()?.calleeExpression?.text in indexedLoopCalls

/**
 * Returns the call expression that owns this lambda argument.
 */
internal fun KtLambdaExpression.containingCallExpression(): KtCallExpression? =
    when (val parent = parent) {
        is KtLambdaArgument -> parent.parent as? KtCallExpression
        is KtValueArgument -> parent.parent?.parent as? KtCallExpression
        else -> null
    }

/**
 * Returns true when this block is the body of a lambda argument passed to [call].
 */
internal fun KtBlockExpression.isLambdaBodyOfCall(call: FiktionCall): Boolean {
    val lambda = getStrictParentOfType<KtLambdaExpression>() ?: return false
    if (lambda.bodyExpression != this) return false
    return lambda.containingCallExpression()?.hasCallee(call) == true
}

/**
 * Returns true when this expression is inside a Fiktion configuration scope.
 */
internal fun KtExpression.isInsideFiktionConfigurationScope(): Boolean =
    generateSequence(parent) { element -> element.parent }
        .any { element ->
            when (element) {
                is KtLambdaExpression -> element.isFiktionConfigurationLambda()
                is KtNamedFunction -> element.name == FIKTION_CONFIGURATION_FUNCTION_NAME
                else -> false
            }
        }

private fun KtLambdaExpression.isFiktionConfigurationLambda(): Boolean {
    val call = containingCallExpression() ?: return false
    if (call.calleeExpression?.text == FIKTION_OBJECT_NAME) return true
    val qualified = call.parent as? KtDotQualifiedExpression ?: return false
    return qualified.selectorExpression == call &&
        qualified.receiverExpression.text == FIKTION_OBJECT_NAME &&
        call.calleeExpression?.text == FIKTION_CONFIGURE_CALL
}

/**
 * Returns the number of type arguments requested by this `typeFamily<T>()` call.
 */
internal fun KtCallExpression.typeFamilyTypeArgumentCount(): Int? {
    if (!hasCallee(FiktionCall.TypeFamily)) return null
    val typeElement =
        typeArgumentList
            ?.arguments
            ?.singleOrNull()
            ?.typeReference
            ?.typeElement ?: return null
    return typeElement.genericTypeArgumentCount()?.takeIf { count -> count > 0 }
}

private fun KtTypeElement.genericTypeArgumentCount(): Int? =
    when (this) {
        is KtUserType -> typeArguments.size
        is KtNullableType -> innerType?.genericTypeArgumentCount()
        else -> null
    }

private val KtFile.fiktionConfigNames: Set<String>
    get() =
        importDirectives
            .mapNotNull { import ->
                if (import.importedFqName?.asString() == FIKTION_CONFIG_FQ_NAME) {
                    import.aliasName ?: FIKTION_CONFIG_NAME
                } else {
                    null
                }
            }.toSet() + FIKTION_CONFIG_NAME

private const val FIKTION_CONFIG_FQ_NAME = "dev.s7a.fiktion.FiktionConfig"
private const val FIKTION_CONFIG_NAME = "FiktionConfig"
private const val FIKTION_CONFIGURE_CALL = "configure"
private const val FIKTION_CONFIGURATION_FUNCTION_NAME = "configure"
private const val FIKTION_OBJECT_NAME = "Fiktion"
private val FIXED_VALUE_GENERATES_CALLS = setOf(FiktionCall.Type, FiktionCall.Property, FiktionCall.Name)
private val INVOKE_BASED_RULE_TARGET_CALLS = setOf(FiktionCall.Type, FiktionCall.Name)

internal val FIKTION_GENERATOR_OPERATIONS =
    setOf(
        FiktionOperation.Generates,
        FiktionOperation.GeneratesBy,
        FiktionOperation.GeneratesIn,
        FiktionOperation.GeneratesOneOf,
    )

internal val FIKTION_GENERATOR_RULE_TARGET_CALLS =
    setOf(
        FiktionCall.Name,
        FiktionCall.Property,
        FiktionCall.Type,
        FiktionCall.TypeFamily,
    )

internal val FIKTION_CONFIG_RULE_TARGET_CALLS =
    setOf(
        FiktionCall.Name,
        FiktionCall.Property,
        FiktionCall.Type,
    )

internal val DEFAULT_INDEXED_LOOP_CALLS =
    setOf(
        "Array",
        "BooleanArray",
        "ByteArray",
        "CharArray",
        "DoubleArray",
        "FloatArray",
        "IntArray",
        "List",
        "LongArray",
        "MutableList",
        "ShortArray",
        "UByteArray",
        "UIntArray",
        "ULongArray",
        "UShortArray",
        "repeat",
    )

internal val DEFAULT_LOOP_CALLS =
    DEFAULT_INDEXED_LOOP_CALLS +
        setOf(
            "all",
            "any",
            "associate",
            "associateBy",
            "associateWith",
            "count",
            "filter",
            "filterIndexed",
            "flatMap",
            "fold",
            "forEach",
            "forEachIndexed",
            "map",
            "mapIndexed",
            "none",
            "onEach",
            "reduce",
            "sumOf",
        )

private fun KtLambdaExpression.isLoopLambda(loopCalls: Set<String>): Boolean =
    containingCallExpression()?.calleeExpression?.text in loopCalls
