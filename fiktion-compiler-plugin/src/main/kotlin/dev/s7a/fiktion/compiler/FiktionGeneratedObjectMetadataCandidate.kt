package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Object metadata declaration that can be generated for one Kotlin data class.
 */
internal data class FiktionGeneratedObjectMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Concrete type represented by this metadata.
     */
    val type: IrType,
    /**
     * Primary constructor used to instantiate this class.
     */
    val constructor: IrConstructor,
    /**
     * Fully qualified class name.
     */
    override val className: String,
    /**
     * Constructor properties in invocation order.
     */
    val properties: List<FiktionGeneratedMetadataPropertyCandidate>,
) : FiktionGeneratedMetadataCandidate
