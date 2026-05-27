package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeNullability
import dev.s7a.fiktion.runtime.FakePath
import dev.s7a.fiktion.runtime.FakeProperty
import dev.s7a.fiktion.runtime.FakeType
import kotlin.reflect.KType

/**
 * Converts this Kotlin type to runtime metadata.
 */
internal fun KType.toFakeType(): FakeType =
    FakeType(
        id = toString(),
        displayName = toString(),
    )

/**
 * Converts this stable type id to runtime metadata.
 */
internal fun String.toFakeType(): FakeType =
    FakeType(
        id = this,
        displayName = this,
    )

/**
 * Converts this Kotlin type to runtime nullability metadata.
 */
internal fun KType.toFakeNullability(): FakeNullability =
    if (isMarkedNullable) {
        FakeNullability.NULLABLE
    } else {
        FakeNullability.NON_NULL
    }

/**
 * Converts this generation request to runtime path metadata.
 */
internal fun GenerationRequest.toFakePath(): FakePath {
    val ownerType = owner?.toFakeType() ?: type.toFakeType()
    val valueType = type.toFakeType()
    val valueNullability = type.toFakeNullability()
    val properties =
        if (pathSegments.isNotEmpty()) {
            pathSegments.mapIndexed { index, segment ->
                val isCurrentValue = index == pathSegments.lastIndex
                FakeProperty(
                    owner = segment.ownerId?.toFakeType() ?: FakeType.Unknown,
                    name = segment.name,
                    type = if (isCurrentValue) valueType else segment.valueId?.toFakeType() ?: FakeType.Unknown,
                    nullability = if (isCurrentValue) valueNullability else FakeNullability.UNKNOWN,
                )
            }
        } else {
            propertyName
                ?.let { name ->
                    listOf(
                        FakeProperty(
                            owner = ownerType,
                            name = name,
                            type = valueType,
                            nullability = valueNullability,
                        ),
                    )
                }.orEmpty()
        }

    return FakePath(
        properties,
    )
}
