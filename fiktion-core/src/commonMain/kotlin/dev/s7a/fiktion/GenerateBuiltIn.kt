package dev.s7a.fiktion

import dev.s7a.fiktion.generators.string
import dev.s7a.fiktion.runtime.CannotGenerateException
import dev.s7a.fiktion.runtime.FakeContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Generates values for primitive built-in types.
 */
internal fun generateBuiltIn(
    type: KType,
    context: FakeContext,
): Any? =
    when (type) {
        typeOf<String>() -> {
            context.string()
        }

        typeOf<Int>() -> {
            context.random.nextInt()
        }

        typeOf<Long>() -> {
            context.random.nextLong()
        }

        typeOf<Boolean>() -> {
            context.random.nextBoolean()
        }

        typeOf<Double>() -> {
            context.random.nextDouble()
        }

        else -> {
            throw CannotGenerateException(
                "Cannot generate $type because no generation rule or metadata is registered for this type. " +
                    "Register metadata for $type, or add an explicit rule: type<$type>() generatesBy { ... }.",
            )
        }
    }
