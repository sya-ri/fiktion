package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrClass

/**
 * Metadata declaration that can be generated for one Kotlin class.
 */
internal sealed interface FiktionGeneratedMetadataCandidate {
    /**
     * IR class represented by this candidate.
     */
    val irClass: IrClass

    /**
     * Fully qualified class name.
     */
    val className: String
}
