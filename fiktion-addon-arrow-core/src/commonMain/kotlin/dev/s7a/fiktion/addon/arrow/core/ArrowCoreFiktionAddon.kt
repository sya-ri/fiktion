@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.arrow.core

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.addon.arrow.core.generators.either
import dev.s7a.fiktion.addon.arrow.core.generators.ior
import dev.s7a.fiktion.addon.arrow.core.generators.nonEmptyList
import dev.s7a.fiktion.addon.arrow.core.generators.nonEmptySet
import dev.s7a.fiktion.addon.arrow.core.generators.option
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy

/**
 * Fiktion add-on that contributes generation rules for Arrow Core types.
 */
public object ArrowCoreFiktionAddon : FiktionAddon {
    override val id: String = "arrow-core"

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
