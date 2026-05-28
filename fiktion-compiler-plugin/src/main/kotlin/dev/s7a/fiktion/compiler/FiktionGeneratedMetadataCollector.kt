package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
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
        if (isExpect || isInner) return null
        val className = fqNameWhenAvailable?.asString().orEmpty()
        if (className.isBlank()) return null

        if (modality == Modality.SEALED) {
            return FiktionGeneratedSealedMetadataCandidate(
                irClass = this,
                className = className,
                subtypes = concreteSealedSubtypes(),
            )
        }

        if (kind == ClassKind.ENUM_CLASS) {
            return FiktionGeneratedEnumMetadataCandidate(
                irClass = this,
                className = className,
                entries = declarations.filterIsInstance<IrEnumEntry>(),
            )
        }

        if (kind == ClassKind.OBJECT) {
            return FiktionGeneratedSingletonMetadataCandidate(
                irClass = this,
                className = className,
            )
        }

        if (kind != ClassKind.CLASS || modality == Modality.ABSTRACT) return null
        val valueClass = isValue || valueClassRepresentation != null
        val constructor = declarations.filterIsInstance<IrConstructor>().firstOrNull { constructor -> constructor.isPrimary } ?: return null
        val parameters = constructor.parameters.filter { parameter -> parameter.kind == IrParameterKind.Regular }
        if (valueClass && parameters.size != 1) return null
        val properties =
            parameters.map { parameter ->
                FiktionGeneratedMetadataPropertyCandidate(
                    parameter = parameter,
                    name = parameter.name.asString(),
                    type = parameter.type.render(),
                    hasDefault = parameter.defaultValue != null,
                )
            }

        return if (valueClass) {
            FiktionGeneratedValueMetadataCandidate(
                irClass = this,
                constructor = constructor,
                className = className,
                property = properties.single(),
            )
        } else {
            FiktionGeneratedObjectMetadataCandidate(
                irClass = this,
                constructor = constructor,
                className = className,
                properties = properties,
            )
        }
    }

    /**
     * Returns concrete sealed subtype leaves for this class.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrClass.concreteSealedSubtypes(): List<IrClass> =
        sealedSubclasses.flatMap { symbol ->
            val subtype = symbol.owner
            if (subtype.modality == Modality.SEALED) {
                subtype.concreteSealedSubtypes()
            } else {
                listOf(subtype)
            }
        }
}
