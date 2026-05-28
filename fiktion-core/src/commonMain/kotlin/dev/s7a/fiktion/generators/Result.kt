package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.Probability
import dev.s7a.fiktion.percent

/**
 * Generates a successful result from [value].
 */
public fun <T> FakeContext.successResult(value: FakeContext.() -> T): Result<T> = Result.success(value(childContext(index = 0)))

/**
 * Generates a failed result from [exception].
 */
public fun <T> FakeContext.failureResult(
    exception: FakeContext.() -> Throwable = { IllegalStateException("Generated failure for ${type.displayName}") },
): Result<T> = Result.failure(exception(childContext(index = 0)))

/**
 * Generates a result that fails according to [failureProbability].
 */
public fun <T> FakeContext.result(
    failureProbability: Probability = 50.percent,
    exception: FakeContext.() -> Throwable = { IllegalStateException("Generated failure for ${type.displayName}") },
    value: FakeContext.() -> T,
): Result<T> =
    if (double() < failureProbability.value) {
        failureResult(exception = exception)
    } else {
        successResult(value = value)
    }
