package dev.s7a.fiktion.detekt

import dev.detekt.api.modifiedText
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.psiUtil.endOffset

internal data class TextReplacement(
    val startOffset: Int,
    val endOffset: Int,
    val text: String,
)

internal fun KtFile.applyReplacements(replacements: List<TextReplacement>) {
    if (replacements.isEmpty()) return
    modifiedText =
        replacements
            .sortedByDescending { replacement -> replacement.startOffset }
            .fold(text) { text, replacement ->
                text.replaceRange(replacement.startOffset, replacement.endOffset, replacement.text)
            }
}

internal fun KtFile.applyReplacements(
    replacements: List<TextReplacement>,
    requiredImports: Set<String>,
) {
    if (replacements.isEmpty()) return
    applyReplacements(
        replacements +
            requiredImports
                .mapNotNull { fqName -> importReplacement(fqName) },
    )
}

private fun KtFile.importReplacement(fqName: String): TextReplacement? {
    if (hasImportFor(fqName)) return null

    val imports = importDirectives
    val insertionOffset = imports.lastOrNull()?.endOffset ?: packageDirective?.takeIf { it.text.isNotBlank() }?.endOffset ?: 0
    val importText =
        when {
            imports.isNotEmpty() -> "\nimport $fqName"
            packageDirective?.text?.isNotBlank() == true -> "\n\nimport $fqName"
            else -> "import $fqName\n\n"
        }
    return TextReplacement(
        startOffset = insertionOffset,
        endOffset = insertionOffset,
        text = importText,
    )
}

private fun KtFile.hasImportFor(fqName: String): Boolean = importDirectives.any { import -> import.matches(fqName) }

private fun KtImportDirective.matches(fqName: String): Boolean {
    val imported = importedFqName?.asString() ?: return false
    return imported == fqName || (isAllUnder && imported == fqName.substringBeforeLast('.'))
}
