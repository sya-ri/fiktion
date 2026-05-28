package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression

/**
 * Local constructor function and callable value passed to runtime metadata.
 */
internal data class ConstructorLambda(
    /**
     * Local function that constructs the target class.
     */
    val function: IrSimpleFunction,
    /**
     * Function reference expression for [function].
     */
    val reference: IrExpression,
)
