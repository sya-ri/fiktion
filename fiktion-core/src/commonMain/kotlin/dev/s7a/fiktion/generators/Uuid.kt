@file:OptIn(ExperimentalUuidApi::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Generates a deterministic UUID from the current fake context seed.
 */
public fun FakeContext.uuid(): Uuid =
    Uuid.fromLongs(
        mostSignificantBits = long(),
        leastSignificantBits = long(),
    )
