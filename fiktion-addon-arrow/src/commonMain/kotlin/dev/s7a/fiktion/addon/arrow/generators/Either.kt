package dev.s7a.fiktion.addon.arrow.generators

import arrow.core.Either
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generators.boolean

/**
 * Generates an Arrow either using [left] or [right].
 */
public fun <L, R> FakeContext.either(
    left: FakeContext.() -> L,
    right: FakeContext.() -> R,
): Either<L, R> =
    if (boolean()) {
        Either.Left(left(childContext(index = 0)))
    } else {
        Either.Right(right(childContext(index = 1)))
    }

/**
 * Generates an Arrow either from the requested type arguments.
 */
internal fun TypeFamilyGenerationContext.either(): Either<Any?, Any?> =
    if (boolean()) {
        Either.Left(fake(0))
    } else {
        Either.Right(fake(1))
    }
