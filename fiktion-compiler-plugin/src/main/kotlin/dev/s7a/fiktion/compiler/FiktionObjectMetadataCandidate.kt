package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor

/**
 * Object metadata declaration that can be generated for one Kotlin class.
 */
internal data class FiktionObjectMetadataCandidate(
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
     * Constructor properties in invocation order.
     */
    val properties: List<FiktionObjectPropertyCandidate>,
)
