package dev.petuska.gtk.compose.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.API_CONFIGURATION_NAME
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME

class ComposePluginLite : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true

    override fun getCompilerPluginId() = "androidx.compose.compiler.plugins.kotlin"

    override fun getPluginArtifact() = SubpluginArtifact(
        "org.jetbrains.compose.compiler",
        "compiler",
//        BuildConfig.composeVersion,
        "1.4.5",
    )

    override fun getPluginArtifactForNative(): SubpluginArtifact =SubpluginArtifact(
        "org.jetbrains.compose.compiler",
        "compiler-hosted",
//        BuildConfig.composeVersion,
        "1.4.5",
    )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { emptyList() }
    }
}