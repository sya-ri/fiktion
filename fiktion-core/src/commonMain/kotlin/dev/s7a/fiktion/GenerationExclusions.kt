package dev.s7a.fiktion

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Candidate exclusions attached to a generation spec.
 */
internal data class GenerationExclusions(
    val values: List<Any?> = emptyList(),
    val valuePredicates: List<(Any?) -> Boolean> = emptyList(),
    val classes: List<KClass<*>> = emptyList(),
    val types: List<KType> = emptyList(),
    val typePredicates: List<(KType) -> Boolean> = emptyList(),
) {
    val hasValueExclusions: Boolean
        get() = values.isNotEmpty() || valuePredicates.isNotEmpty()

    val hasTypeExclusions: Boolean
        get() = classes.isNotEmpty() || types.isNotEmpty() || typePredicates.isNotEmpty()

    val isNotEmpty: Boolean
        get() = hasValueExclusions || hasTypeExclusions

    fun plusValue(value: Any?): GenerationExclusions = copy(values = values + value)

    fun plusValues(values: Iterable<Any?>): GenerationExclusions = copy(values = this.values + values)

    fun plusValuePredicate(predicate: (Any?) -> Boolean): GenerationExclusions = copy(valuePredicates = valuePredicates + predicate)

    fun plusClass(type: KClass<*>): GenerationExclusions = copy(classes = classes + type)

    fun plusClasses(types: Iterable<KClass<*>>): GenerationExclusions = copy(classes = classes + types)

    fun plusType(type: KType): GenerationExclusions = copy(types = types + type)

    fun plusTypes(types: Iterable<KType>): GenerationExclusions = copy(types = this.types + types)

    fun plusTypePredicate(predicate: (KType) -> Boolean): GenerationExclusions = copy(typePredicates = typePredicates + predicate)

    fun excludesValue(value: Any?): Boolean =
        values.any { excluded -> excluded == value } || valuePredicates.any { predicate -> predicate(value) }

    fun excludesType(type: KType): Boolean =
        types.any { excluded -> excluded == type } ||
            classes.any { excluded -> excluded == type.classifier } ||
            typePredicates.any { predicate -> predicate(type) }
}
