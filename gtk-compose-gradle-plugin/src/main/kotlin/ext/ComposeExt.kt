package dev.petuska.gtk.compose.gradle.plugin.ext

import dev.petuska.gtk.compose.gradle.plugin.ComposePlugin
import dev.petuska.gtk.compose.gradle.plugin.utils.maybeCreate
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface ComposeExt : ExtensionAware {
    /**
     * Custom Compose Compiler maven coordinates. You can set it using values provided
     * by [ComposePlugin.CompilerDependencies]:
     * ```
     * kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.7.20"))
     * ```
     * or set it to the Jetpack Compose Compiler:
     * ```
     * kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0-alpha02")
     * ```
     * (see available versions here: https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility)
     */
    val kotlinCompilerPlugin: Property<String?>

    /**
     * List of the arguments applied to the Compose Compiler. Example:
     * ```
     * kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.7.21")
     * ```
     * See all available arguments here:
     * https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/compiler-hosted/src/main/java/androidx/compose/compiler/plugins/kotlin/ComposePlugin.kt
     */
    val kotlinCompilerPluginArgs: ListProperty<String>
}

internal inline val ComposeExt.dependencies
    get() = extensions.maybeCreate<ComposePlugin.Dependencies>("dependencies") {

    }

internal val GtkExt.compose
    get() = extensions.maybeCreate<ComposeExt>("compose") {

    }