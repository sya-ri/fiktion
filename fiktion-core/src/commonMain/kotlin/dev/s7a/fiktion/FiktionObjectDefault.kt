package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi

/**
 * Constructor argument marker that asks generated metadata to use the property's default value.
 */
@ExperimentalFiktionApi
public data object FiktionObjectDefault : FiktionObjectArgument
