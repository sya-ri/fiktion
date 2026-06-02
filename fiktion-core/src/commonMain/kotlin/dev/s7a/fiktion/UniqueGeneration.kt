package dev.s7a.fiktion

/**
 * Generates candidate elements for a set-like collection according to [strategy].
 */
public fun <T> generateUniqueElements(
    size: Int,
    strategy: UniqueElementStrategy,
    element: (Int) -> T,
): List<T> {
    require(size >= 0) { "Unique element size must be non-negative, but was $size." }

    return when (strategy) {
        UniqueElementStrategy.BestEffort -> {
            List(size, element)
        }

        is UniqueElementStrategy.Exact -> {
            generateExactUniqueElements(
                size = size,
                maxAttemptsPerElement = strategy.maxAttemptsPerElement,
                element = element,
            )
        }
    }
}

private fun <T> generateExactUniqueElements(
    size: Int,
    maxAttemptsPerElement: Int,
    element: (Int) -> T,
): List<T> {
    if (size == 0) return emptyList()

    val maxAttempts = maxAttempts(size = size, maxAttemptsPerElement = maxAttemptsPerElement)
    val seen = mutableSetOf<T>()
    val elements = mutableListOf<T>()
    var attempt = 0

    while (elements.size < size && attempt < maxAttempts) {
        val candidate = element(attempt)
        if (seen.add(candidate)) {
            elements += candidate
        }
        attempt += 1
    }

    if (elements.size != size) {
        throw CannotGenerateException(
            "Cannot generate $size unique elements after $attempt attempts; " +
                "generated ${elements.size} distinct values. " +
                "Increase maxAttemptsPerElement or use UniqueElementStrategy.BestEffort.",
        )
    }

    return elements
}

private fun maxAttempts(
    size: Int,
    maxAttemptsPerElement: Int,
): Int =
    if (size > Int.MAX_VALUE / maxAttemptsPerElement) {
        Int.MAX_VALUE
    } else {
        size * maxAttemptsPerElement
    }
