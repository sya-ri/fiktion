package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor

/**
 * Value metadata declaration that can be generated for one Kotlin value class.
 */
internal data class FiktionGeneratedValueMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Primary constructor used to instantiate this class.
     */
    val constructor: IrConstructor,
    /**
     * Fully qualified class name.
     */
    override val className: String,
    /**
     * Single underlying constructor property.
     */
    val property: FiktionGeneratedMetadataPropertyCandidate,
) : FiktionGeneratedMetadataCandidate
