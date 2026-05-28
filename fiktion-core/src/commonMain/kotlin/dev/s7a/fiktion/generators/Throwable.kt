package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a throwable.
 */
public fun FakeContext.throwable(message: String = string()): Throwable = Throwable(message)

/**
 * Generates an error.
 */
public fun FakeContext.error(message: String = string()): Error = Error(message)

/**
 * Generates an exception.
 */
public fun FakeContext.exception(message: String = string()): Exception = Exception(message)

/**
 * Generates a runtime exception.
 */
public fun FakeContext.runtimeException(message: String = string()): RuntimeException = RuntimeException(message)

/**
 * Generates an illegal state exception.
 */
public fun FakeContext.illegalStateException(message: String = string()): IllegalStateException = IllegalStateException(message)

/**
 * Generates an illegal argument exception.
 */
public fun FakeContext.illegalArgumentException(message: String = string()): IllegalArgumentException = IllegalArgumentException(message)

/**
 * Generates an index out of bounds exception.
 */
public fun FakeContext.indexOutOfBoundsException(message: String = string()): IndexOutOfBoundsException = IndexOutOfBoundsException(message)

/**
 * Generates a concurrent modification exception.
 */
public fun FakeContext.concurrentModificationException(message: String = string()): ConcurrentModificationException =
    ConcurrentModificationException(message)

/**
 * Generates an unsupported operation exception.
 */
public fun FakeContext.unsupportedOperationException(message: String = string()): UnsupportedOperationException =
    UnsupportedOperationException(message)

/**
 * Generates a number format exception.
 */
public fun FakeContext.numberFormatException(message: String = string()): NumberFormatException = NumberFormatException(message)

/**
 * Generates a null pointer exception.
 */
public fun FakeContext.nullPointerException(message: String = string()): NullPointerException = NullPointerException(message)

/**
 * Generates a class cast exception.
 */
public fun FakeContext.classCastException(message: String = string()): ClassCastException = ClassCastException(message)

/**
 * Generates an assertion error.
 */
public fun FakeContext.assertionError(message: String = string()): AssertionError = AssertionError(message)

/**
 * Generates a no such element exception.
 */
public fun FakeContext.noSuchElementException(message: String = string()): NoSuchElementException = NoSuchElementException(message)

/**
 * Generates an arithmetic exception.
 */
public fun FakeContext.arithmeticException(message: String = string()): ArithmeticException = ArithmeticException(message)
