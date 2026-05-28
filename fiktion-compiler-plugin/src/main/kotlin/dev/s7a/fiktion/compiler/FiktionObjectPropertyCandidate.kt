package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrValueParameter

/**
 * Constructor property metadata that can be generated for one Kotlin value parameter.
 */
internal data class FiktionObjectPropertyCandidate(
    /**
     * IR constructor parameter represented by this property.
     */
    val parameter: IrValueParameter,
    /**
     * Constructor parameter name.
     */
    val name: String,
    /**
     * Constructor parameter type rendered from IR.
     */
    val type: String,
    /**
     * Whether the constructor parameter declares a default value.
     */
    val hasDefault: Boolean,
)
