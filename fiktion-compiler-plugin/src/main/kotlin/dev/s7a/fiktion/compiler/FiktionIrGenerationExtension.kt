package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * IR generation extension that will emit Fiktion metadata declarations.
 */
public class FiktionIrGenerationExtension(
    private val automaticAddons: List<String> = emptyList(),
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val candidates = FiktionGeneratedMetadataCollector().collect(moduleFragment)
        FiktionGeneratedMetadataRegistrar(pluginContext, candidates, automaticAddons).registerBeforeFakeCalls(moduleFragment)
    }
}
