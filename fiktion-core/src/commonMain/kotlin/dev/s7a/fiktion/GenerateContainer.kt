package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates built-in container values, or returns `null` when [type] is not a supported container.
 */
internal fun generateContainer(
    type: KType,
    context: FakeContext,
    config: FiktionConfig,
): Any? =
    when (type.classifier) {
        List::class,
        MutableList::class,
        Set::class,
        MutableSet::class,
        -> {
            generateAutomaticCollection(
                request = GenerationRequest(type = type),
                config = config,
                seed = context.seed,
                depth = context.depth,
                context = context,
                elementType = containerTypeArgument(type = type, index = 0),
                sizeRange = DEFAULT_COLLECTION_SIZE_RANGE,
            )
        }

        Map::class,
        MutableMap::class,
        -> {
            generateAutomaticMap(
                config = config,
                seed = context.seed,
                depth = context.depth,
                context = context,
                keyType = containerTypeArgument(type = type, index = 0),
                valueType = containerTypeArgument(type = type, index = 1),
                sizeRange = DEFAULT_MAP_SIZE_RANGE,
            )
        }

        Sequence::class -> {
            generateAutomaticSequence(
                elementType = containerTypeArgument(type = type, index = 0),
                context = context,
                config = config,
            )
        }

        else -> null
    }

/**
 * Generates a finite sequence by automatically generating each element.
 */
private fun generateAutomaticSequence(
    elementType: KType,
    context: FakeContext,
    config: FiktionConfig,
): Sequence<Any?> {
    val count = DEFAULT_COLLECTION_SIZE_RANGE.random(context.random)
    return List(count) { index ->
        generateValue(
            request = GenerationRequest(type = elementType),
            config = config,
            seed = context.seed.childSeed(index),
            depth = context.depth + 1,
        )
    }.asSequence()
}

/**
 * Returns [type]'s type argument at [index].
 */
private fun containerTypeArgument(
    type: KType,
    index: Int,
): KType =
    type.arguments.getOrNull(index)?.type
        ?: throw CannotGenerateException("Cannot generate $type because type argument $index is unavailable.")
