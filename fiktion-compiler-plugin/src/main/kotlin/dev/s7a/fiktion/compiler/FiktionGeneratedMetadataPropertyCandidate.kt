package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Constructor property metadata that can be generated for one Kotlin value parameter.
 */
internal data class FiktionGeneratedMetadataPropertyCandidate(
    /**
     * IR constructor parameter represented by this property.
     */
    val parameter: IrValueParameter,
    /**
     * Concrete property type after owner type-argument substitution.
     */
    val type: IrType,
    /**
     * Constructor parameter name.
     */
    val name: String,
    /**
     * Whether the constructor parameter declares a default value.
     */
    val hasDefault: Boolean,
)
