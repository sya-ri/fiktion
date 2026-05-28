package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid

/**
 * Collects classes that can receive generated Fiktion metadata.
 */
internal class FiktionGeneratedMetadataCollector {
    /**
     * Returns metadata candidates found in [moduleFragment].
     */
    fun collect(moduleFragment: IrModuleFragment): List<FiktionGeneratedMetadataCandidate> {
        val candidates = mutableListOf<FiktionGeneratedMetadataCandidate>()

        moduleFragment.accept(
            object : IrVisitorVoid() {
                override fun visitElement(element: IrElement) {
                    element.acceptChildren(this, null)
                }

                override fun visitClass(declaration: IrClass) {
                    declaration.toCandidate()?.let(candidates::add)
                    super.visitClass(declaration)
                }
            },
            null,
        )

        return candidates
    }

    /**
     * Returns a generated metadata candidate for this class, or `null` when the class is outside the supported shape.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrClass.toCandidate(): FiktionGeneratedMetadataCandidate? {
        if (kind != ClassKind.CLASS || isExpect || isInner) return null
        val valueClass = isValue || valueClassRepresentation != null
        if (!isData && !valueClass) return null
        val constructor = declarations.filterIsInstance<IrConstructor>().firstOrNull { constructor -> constructor.isPrimary } ?: return null
        val className = fqNameWhenAvailable?.asString().orEmpty()
        if (className.isBlank()) return null
        val parameters = constructor.parameters.filter { parameter -> parameter.kind == IrParameterKind.Regular }
        if (valueClass && parameters.size != 1) return null

        return FiktionGeneratedMetadataCandidate(
            irClass = this,
            constructor = constructor,
            className = className,
            isValueClass = valueClass,
            properties =
                parameters.map { parameter ->
                    FiktionGeneratedMetadataPropertyCandidate(
                        parameter = parameter,
                        name = parameter.name.asString(),
                        type = parameter.type.render(),
                        hasDefault = parameter.defaultValue != null,
                    )
                },
        )
    }
}
