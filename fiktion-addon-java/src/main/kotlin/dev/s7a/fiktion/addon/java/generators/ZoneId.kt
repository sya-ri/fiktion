package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.time.ZoneId

/**
 * Generates a Java zone id from the available JDK zone ids.
 */
public fun FakeContext.zoneId(): ZoneId = oneOf(JAVA_ZONE_IDS)

private val JAVA_ZONE_IDS: List<ZoneId> =
    ZoneId
        .getAvailableZoneIds()
        .sorted()
        .map { zoneId -> ZoneId.of(zoneId) }
