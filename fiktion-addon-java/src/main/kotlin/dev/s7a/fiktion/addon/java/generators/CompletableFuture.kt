package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import java.util.concurrent.CompletableFuture

/**
 * Generates a completed Java future using [value].
 */
public fun <T> FakeContext.completableFuture(value: FakeContext.() -> T): CompletableFuture<T> = CompletableFuture.completedFuture(value())

/**
 * Generates a completed Java future from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.completableFuture(): CompletableFuture<Any?> = CompletableFuture.completedFuture(fake(0))
