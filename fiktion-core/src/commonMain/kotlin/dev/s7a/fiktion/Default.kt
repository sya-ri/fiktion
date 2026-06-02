package dev.s7a.fiktion

/**
 * Marker used by `generates default` to request a constructor default value.
 */
public data object Default

/**
 * Requests a constructor default value in rule declarations.
 */
public val default: Default = Default
