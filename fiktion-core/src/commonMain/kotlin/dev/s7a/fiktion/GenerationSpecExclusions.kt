package dev.s7a.fiktion

import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Excludes sealed subtype candidates whose classifier is [type] from this generation rule.
 *
 * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
 * excluded.
 */
public infix fun <T> GenerationSpec<T>.excluding(type: KClass<*>): GenerationSpec<T> =
    mutateDefaultSpec { exclusions = exclusions.plusClass(type) }

/**
 * Excludes sealed subtype candidates matching [type] from this generation rule.
 *
 * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
 * excluded.
 */
public infix fun <T> GenerationSpec<T>.excluding(type: KType): GenerationSpec<T> =
    mutateDefaultSpec { exclusions = exclusions.plusType(type) }

/**
 * Excludes sealed subtype candidates whose classifier is one of [types] from this generation rule.
 *
 * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
 * excluded.
 */
@JvmName("excludingClasses")
public infix fun <T> GenerationSpec<T>.excluding(types: Iterable<KClass<*>>): GenerationSpec<T> =
    mutateDefaultSpec { exclusions = exclusions.plusClasses(types) }

/**
 * Excludes sealed subtype candidates matching one of [types] from this generation rule.
 *
 * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
 * excluded.
 */
@JvmName("excludingTypes")
public infix fun <T> GenerationSpec<T>.excluding(types: Iterable<KType>): GenerationSpec<T> =
    mutateDefaultSpec { exclusions = exclusions.plusTypes(types) }

/**
 * Excludes sealed subtype candidates matching [predicate] from this generation rule.
 *
 * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
 * excluded.
 */
@JvmName("excludingTypePredicate")
public infix fun <T> GenerationSpec<T>.excluding(predicate: (KType) -> Boolean): GenerationSpec<T> =
    mutateDefaultSpec { exclusions = exclusions.plusTypePredicate(predicate) }

private inline fun <T> GenerationSpec<T>.mutateDefaultSpec(configure: DefaultGenerationSpec<T>.() -> Unit): GenerationSpec<T> {
    requireFiktionConfiguration(this is DefaultGenerationSpec<T>) {
        "Exclusions can only be applied to Fiktion generation specs."
    }
    val spec = this as DefaultGenerationSpec<T>
    spec.configure()
    return this
}
