package dev.petuska.gtk.compose.gradle.plugin

import dev.petuska.gtk.compose.gradle.plugin.utils.nullableProperty
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class ComposeExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project
) : ExtensionAware {
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
    val kotlinCompilerPlugin: Property<String?> = objects.nullableProperty()

    /**
     * List of the arguments applied to the Compose Compiler. Example:
     * ```
     * kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.7.21")
     * ```
     * See all available arguments here:
     * https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/compiler-hosted/src/main/java/androidx/compose/compiler/plugins/kotlin/ComposePlugin.kt
     */
    val kotlinCompilerPluginArgs: ListProperty<String> = objects.listProperty(String::class.java)

    val dependencies = ComposePlugin.Dependencies(project)
}
