package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry

/**
 * Enum metadata declaration that can be generated for one Kotlin enum class.
 */
internal data class FiktionGeneratedEnumMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Fully qualified class name.
     */
    override val className: String,
    /**
     * Enum entries selectable during generation.
     */
    val entries: List<IrEnumEntry>,
) : FiktionGeneratedMetadataCandidate
