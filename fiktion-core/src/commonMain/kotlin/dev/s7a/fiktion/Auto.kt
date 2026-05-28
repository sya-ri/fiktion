package dev.s7a.fiktion

/**
 * Marker used by `generates auto` to request Fiktion's automatic generation.
 */
public data object Auto

/**
 * Requests Fiktion's automatic generation in rule declarations.
 */
public val auto: Auto = Auto
