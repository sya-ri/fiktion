package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ThrowableTest {
    @Test
    fun `throwable generates configured message`() {
        val fiktion = Fiktion { type<Throwable>() generatesBy { throwable(message = "failure") } }

        assertEquals("failure", fiktion.fake<Throwable>(seed = 1).message)
    }

    @Test
    fun `exception generators support configured messages`() {
        val fiktion =
            Fiktion {
                type<Error>() generatesBy { error(message = "error") }
                type<Exception>() generatesBy { exception(message = "exception") }
                type<RuntimeException>() generatesBy { runtimeException(message = "runtime") }
                type<IllegalStateException>() generatesBy { illegalStateException(message = "state") }
                type<IllegalArgumentException>() generatesBy { illegalArgumentException(message = "argument") }
                type<IndexOutOfBoundsException>() generatesBy { indexOutOfBoundsException(message = "index") }
                type<ConcurrentModificationException>() generatesBy { concurrentModificationException(message = "concurrent") }
                type<UnsupportedOperationException>() generatesBy { unsupportedOperationException(message = "unsupported") }
                type<NumberFormatException>() generatesBy { numberFormatException(message = "number") }
                type<NullPointerException>() generatesBy { nullPointerException(message = "null") }
                type<ClassCastException>() generatesBy { classCastException(message = "cast") }
                type<AssertionError>() generatesBy { assertionError(message = "assertion") }
                type<NoSuchElementException>() generatesBy { noSuchElementException(message = "element") }
                type<ArithmeticException>() generatesBy { arithmeticException(message = "arithmetic") }
            }

        assertEquals("error", fiktion.fake<Error>(seed = 1).message)
        assertEquals("exception", fiktion.fake<Exception>(seed = 1).message)
        assertEquals("runtime", fiktion.fake<RuntimeException>(seed = 1).message)
        assertEquals("state", fiktion.fake<IllegalStateException>(seed = 1).message)
        assertEquals("argument", fiktion.fake<IllegalArgumentException>(seed = 1).message)
        assertEquals("index", fiktion.fake<IndexOutOfBoundsException>(seed = 1).message)
        assertEquals("concurrent", fiktion.fake<ConcurrentModificationException>(seed = 1).message)
        assertEquals("unsupported", fiktion.fake<UnsupportedOperationException>(seed = 1).message)
        assertEquals("number", fiktion.fake<NumberFormatException>(seed = 1).message)
        assertEquals("null", fiktion.fake<NullPointerException>(seed = 1).message)
        assertEquals("cast", fiktion.fake<ClassCastException>(seed = 1).message)
        assertEquals("assertion", fiktion.fake<AssertionError>(seed = 1).message)
        assertEquals("element", fiktion.fake<NoSuchElementException>(seed = 1).message)
        assertEquals("arithmetic", fiktion.fake<ArithmeticException>(seed = 1).message)
    }
}
