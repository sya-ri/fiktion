package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass

/**
 * Object metadata declaration that can be generated for one Kotlin singleton object.
 */
internal data class FiktionGeneratedSingletonMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Fully qualified class name.
     */
    override val className: String,
) : FiktionGeneratedMetadataCandidate
