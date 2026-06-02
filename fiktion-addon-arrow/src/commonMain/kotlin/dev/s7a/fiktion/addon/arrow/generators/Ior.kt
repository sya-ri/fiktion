package dev.s7a.fiktion.addon.arrow.generators

import arrow.core.Ior
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generators.int

/**
 * Generates an Arrow inclusive-or using [left] and/or [right].
 */
public fun <L, R> FakeContext.ior(
    left: FakeContext.() -> L,
    right: FakeContext.() -> R,
): Ior<L, R> =
    when (int(0, 2)) {
        0 -> Ior.Left(left(childContext(index = 0)))
        1 -> Ior.Right(right(childContext(index = 1)))
        else -> Ior.Both(left(childContext(index = 0)), right(childContext(index = 1)))
    }

/**
 * Generates an Arrow inclusive-or from the requested type arguments.
 */
internal fun TypeFamilyGenerationContext.ior(): Ior<Any?, Any?> =
    when (int(0, 2)) {
        0 -> {
            Ior.Left(fake(0))
        }

        1 -> {
            Ior.Right(fake(1))
        }

        else -> {
            Ior.Both(
                fake(0),
                fake(1),
            )
        }
    }
