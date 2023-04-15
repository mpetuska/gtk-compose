package dev.petuska.gtk.compose.gradle.plugin

import dev.petuska.gtk.compose.gradle.plugin.ext.compose
import dev.petuska.gtk.compose.gradle.plugin.ext.gtk
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

class ComposeCompilerKotlinSupportPlugin : KotlinCompilerPluginSupportPlugin {
    private lateinit var composeCompilerArtifactProvider: ComposeCompilerArtifactProvider

    override fun apply(target: Project) {
        super.apply(target)
        target.plugins.withType(ComposePlugin::class.java) {

            composeCompilerArtifactProvider = ComposeCompilerArtifactProvider {
                target.gtk.compose.kotlinCompilerPlugin.orNull
                    ?: ComposeCompilerCompatibility.compilerVersionFor(target.getKotlinPluginVersion())
            }
        }
    }

    override fun getCompilerPluginId(): String =
        "androidx.compose.compiler.plugins.kotlin"

    override fun getPluginArtifact(): SubpluginArtifact =
        composeCompilerArtifactProvider.compilerArtifact

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        composeCompilerArtifactProvider.compilerHostedArtifact

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        when (kotlinCompilation.target.platformType) {
            KotlinPlatformType.native -> true
            KotlinPlatformType.common,
            KotlinPlatformType.jvm,
            KotlinPlatformType.js,
            KotlinPlatformType.androidJvm,
            KotlinPlatformType.wasm -> false
        }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val target = kotlinCompilation.target
        return target.project.provider {
            platformPluginOptions[target.platformType] ?: emptyList()
        }
    }

    private val platformPluginOptions = mapOf(
        KotlinPlatformType.js to options("generateDecoys" to "true")
    )

    private fun options(vararg options: Pair<String, String>): List<SubpluginOption> =
        options.map { SubpluginOption(it.first, it.second) }
}