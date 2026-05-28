package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.EnumMap

/**
 * Generates a Java enum map using [key] and [value].
 */
public inline fun <reified K : Enum<K>, V> FakeContext.enumMap(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): EnumMap<K, V> =
    EnumMap<K, V>(K::class.java).apply {
        repeat(size) {
            put(key(), value())
        }
    }

/**
 * Generates a Java enum map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.enumMap(): EnumMap<*, *> {
    val enumClass = enumClass(argumentIndex = 0)
    val values = mutableMapOf<Enum<*>, Any?>()
    map(
        size = int(1..3),
        key = {
            fake(argumentIndex = 0, seedIndex = index)
        },
        value = {
            fake(argumentIndex = 1, seedIndex = index)
        },
    ).forEach { (key, value) ->
        if (key == null) return@forEach
        values[enumClass.cast(key)] = value
    }
    return enumMap(enumClass = enumClass, values = values)
}

@Suppress("UNCHECKED_CAST")
private fun enumMap(
    enumClass: Class<out Enum<*>>,
    values: Map<Enum<*>, Any?>,
): EnumMap<*, *> {
    val result = EnumMap<Nothing, Any?>(enumClass as Class<Nothing>)
    result.putAll(values as Map<Nothing, Any?>)
    return result
}
