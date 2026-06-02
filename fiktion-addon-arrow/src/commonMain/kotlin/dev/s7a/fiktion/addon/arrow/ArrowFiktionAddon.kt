@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.arrow

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.addon.arrow.generators.either
import dev.s7a.fiktion.addon.arrow.generators.ior
import dev.s7a.fiktion.addon.arrow.generators.nonEmptyList
import dev.s7a.fiktion.addon.arrow.generators.nonEmptySet
import dev.s7a.fiktion.addon.arrow.generators.option
import dev.s7a.fiktion.generatesBy

/**
 * Fiktion add-on that contributes generation rules for Arrow Core types.
 */
public object ArrowFiktionAddon : FiktionAddon {
    override val id: String = "arrow"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            typeFamily<Option<*>>() generatesBy {
                option()
            }
            typeFamily<Either<*, *>>() generatesBy {
                either()
            }
            typeFamily<Ior<*, *>>() generatesBy {
                ior()
            }
            typeFamily<NonEmptyList<*>>() generatesBy {
                nonEmptyList()
            }
            typeFamily<NonEmptySet<*>>() generatesBy {
                nonEmptySet()
            }
        }
    }
}
