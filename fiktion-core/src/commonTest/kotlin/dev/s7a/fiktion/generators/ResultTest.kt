package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.percent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResultTest {
    @Test
    fun `successResult generates successful results`() {
        val fiktion =
            Fiktion {
                type<Result<String>>() generatesBy {
                    successResult { "value" }
                }
            }

        assertEquals("value", fiktion.fake<Result<String>>(seed = 1).getOrThrow())
    }

    @Test
    fun `failureResult generates failed results`() {
        val fiktion =
            Fiktion {
                type<Result<String>>() generatesBy {
                    failureResult { IllegalArgumentException("failure") }
                }
            }

        val exception = fiktion.fake<Result<String>>(seed = 1).exceptionOrNull()

        assertIs<IllegalArgumentException>(exception)
        assertEquals("failure", exception.message)
    }

    @Test
    fun `result can force success and failure by probability`() {
        val successFiktion =
            Fiktion {
                type<Result<String>>() generatesBy {
                    result(failureProbability = 0.percent, value = { "value" })
                }
            }
        val failureFiktion =
            Fiktion {
                type<Result<String>>() generatesBy {
                    result(failureProbability = 100.percent, value = { "value" })
                }
            }

        assertTrue(successFiktion.fake<Result<String>>(seed = 1).isSuccess)
        assertTrue(failureFiktion.fake<Result<String>>(seed = 1).isFailure)
    }
}
