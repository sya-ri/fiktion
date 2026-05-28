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
 * Collects data classes that can receive generated object metadata.
 */
internal class FiktionObjectMetadataCollector {
    /**
     * Returns metadata candidates found in [moduleFragment].
     */
    fun collect(moduleFragment: IrModuleFragment): List<FiktionObjectMetadataCandidate> {
        val candidates = mutableListOf<FiktionObjectMetadataCandidate>()

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
     * Returns an object metadata candidate for this class, or `null` when the class is outside the supported shape.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrClass.toCandidate(): FiktionObjectMetadataCandidate? {
        if (!isData || kind != ClassKind.CLASS || isExpect || isInner) return null
        val constructor = declarations.filterIsInstance<IrConstructor>().firstOrNull { constructor -> constructor.isPrimary } ?: return null
        val className = fqNameWhenAvailable?.asString().orEmpty()
        if (className.isBlank()) return null

        return FiktionObjectMetadataCandidate(
            irClass = this,
            constructor = constructor,
            className = className,
            properties =
                constructor.parameters.filter { parameter -> parameter.kind == IrParameterKind.Regular }.map { parameter ->
                    FiktionObjectPropertyCandidate(
                        parameter = parameter,
                        name = parameter.name.asString(),
                        type = parameter.type.render(),
                        hasDefault = parameter.defaultValue != null,
                    )
                },
        )
    }
}
