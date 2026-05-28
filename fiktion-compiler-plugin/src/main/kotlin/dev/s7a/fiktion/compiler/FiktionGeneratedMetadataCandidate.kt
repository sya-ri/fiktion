package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor

/**
 * Metadata declaration that can be generated for one Kotlin class.
 */
internal data class FiktionGeneratedMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    val irClass: IrClass,
    /**
     * Primary constructor used to instantiate this class.
     */
    val constructor: IrConstructor,
    /**
     * Fully qualified class name.
     */
    val className: String,
    /**
     * Whether this candidate represents a Kotlin value class.
     */
    val isValueClass: Boolean,
    /**
     * Constructor properties in invocation order.
     */
    val properties: List<FiktionGeneratedMetadataPropertyCandidate>,
)
