package dev.s7a.fiktion

import kotlin.random.Random
import kotlin.ranges.ClosedRange

internal fun ClosedRange<Int>.random(random: Random): Int {
    requireFiktionConfiguration(start <= endInclusive) { "range must not be empty." }
    val size = endInclusive.toLong() - start.toLong() + 1L
    return if (size == 1L shl 32) {
        random.nextInt()
    } else {
        (start.toLong() + random.nextLong(until = size)).toInt()
    }
}
