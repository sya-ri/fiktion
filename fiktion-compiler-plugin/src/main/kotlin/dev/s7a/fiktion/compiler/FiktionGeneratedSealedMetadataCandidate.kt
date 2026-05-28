package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass

/**
 * Sealed metadata declaration that can be generated for one Kotlin sealed class or interface.
 */
internal data class FiktionGeneratedSealedMetadataCandidate(
    /**
     * IR class represented by this candidate.
     */
    override val irClass: IrClass,
    /**
     * Fully qualified class name.
     */
    override val className: String,
    /**
     * Concrete subtypes selectable during generation.
     */
    val subtypes: List<IrClass>,
) : FiktionGeneratedMetadataCandidate
