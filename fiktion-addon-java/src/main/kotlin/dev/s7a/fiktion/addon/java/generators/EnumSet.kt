package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.EnumSet
import kotlin.reflect.KClass

/**
 * Generates a Java enum set using [element].
 */
public inline fun <reified E : Enum<E>> FakeContext.enumSet(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> E,
): EnumSet<E> =
    EnumSet.noneOf(E::class.java).apply {
        repeat(size) {
            add(element())
        }
    }

/**
 * Generates a Java enum set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.enumSet(): EnumSet<*> {
    val enumClass = enumClass(argumentIndex = 0)
    val values = mutableListOf<Enum<*>>()
    repeat(int(config(FiktionConfig.Collection.size))) { index ->
        fake(argumentIndex = 0, seedIndex = index)?.let { value ->
            values += enumClass.cast(value)
        }
    }
    return enumSet(enumClass = enumClass, values = values)
}

@Suppress("UNCHECKED_CAST")
private fun enumSet(
    enumClass: Class<out Enum<*>>,
    values: Collection<Enum<*>>,
): EnumSet<*> {
    val result = EnumSet.noneOf(enumClass as Class<Nothing>)
    result.addAll(values as Collection<Nothing>)
    return result
}

@Suppress("UNCHECKED_CAST")
internal fun TypeFamilyGenerationContext.enumClass(argumentIndex: Int): Class<out Enum<*>> {
    val classifier =
        argumentType(argumentIndex).classifier as? KClass<*>
            ?: throw CannotGenerateException("Cannot generate enum collection because type argument $argumentIndex is not a class.")
    val javaClass = classifier.java
    if (!javaClass.isEnum) {
        throw CannotGenerateException("Cannot generate enum collection because ${classifier.qualifiedName} is not an enum.")
    }
    return javaClass as Class<out Enum<*>>
}
