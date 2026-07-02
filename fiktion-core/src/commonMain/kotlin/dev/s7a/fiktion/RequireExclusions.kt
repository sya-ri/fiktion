package dev.s7a.fiktion

internal fun requireNoTypeExclusions(
    exclusions: GenerationExclusions,
    target: String,
) {
    requireFiktionConfiguration(!exclusions.hasTypeExclusions) {
        "Type exclusions can only be applied to sealed subtype selection, but $target does not select sealed subtypes."
    }
}

internal fun requireNoValueExclusions(
    exclusions: GenerationExclusions,
    target: String,
) {
    requireFiktionConfiguration(!exclusions.hasValueExclusions) {
        "Value exclusions can only be applied to value candidate selection, but $target does not select value candidates."
    }
}
