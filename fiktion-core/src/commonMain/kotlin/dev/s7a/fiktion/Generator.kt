package dev.s7a.fiktion

/**
 * Custom value generator executed with a [FakeContext].
 */
public typealias Generator<T> = FakeContext.() -> T
