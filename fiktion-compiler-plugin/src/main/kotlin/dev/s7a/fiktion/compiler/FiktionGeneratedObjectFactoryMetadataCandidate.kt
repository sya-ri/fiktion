package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Object metadata declaration generated from an explicitly selected factory function.
 */
internal data class FiktionGeneratedObjectFactoryMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Concrete type represented by this metadata.
     */
    val type: IrType,
    /**
     * Factory function used to instantiate this class.
     */
    val factory: IrSimpleFunction,
    /**
     * Fully qualified class name.
     */
    override val className: String,
    /**
     * Factory parameters in invocation order.
     */
    val properties: List<FiktionGeneratedMetadataPropertyCandidate>,
) : FiktionGeneratedMetadataCandidate
