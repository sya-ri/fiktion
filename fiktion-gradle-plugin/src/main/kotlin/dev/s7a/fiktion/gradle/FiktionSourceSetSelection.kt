package dev.s7a.fiktion.gradle

/**
 * Returns whether Fiktion should be enabled for [sourceSetName].
 */
internal fun FiktionExtension.isEnabledFor(sourceSetName: String): Boolean {
    sourceSets
        .findByName(sourceSetName)
        ?.enabled
        ?.orNull
        ?.let { enabled -> return enabled }
    if (enabled.getOrElse(false)) return true
    return testEnabled.getOrElse(true) && sourceSetName.endsWith("Test")
}
