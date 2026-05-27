package dev.s7a.fiktion.runtime

/**
 * Custom value generator executed with a [FakeContext].
 */
public typealias Generator<T> = FakeContext.() -> T
