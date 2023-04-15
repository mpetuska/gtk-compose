package dev.petuska.gtk.compose.gradle.plugin

import dev.petuska.gtk.compose.gradle.plugin.config.attachKnTarget
import dev.petuska.gtk.compose.gradle.plugin.config.detachKnTarget
import dev.petuska.gtk.compose.gradle.plugin.config.gtkSupported
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class GtkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            project.afterKmpPlugin(project.extensions.getByType())
        }
    }

    private fun Project.afterKmpPlugin(kmp: KotlinMultiplatformExtension) {
        kmp.targets.whenObjectAdded {
            if (this is KotlinNativeTarget && gtkSupported) attachKnTarget(this)
        }
        kmp.targets.whenObjectRemoved {
            if (this is KotlinNativeTarget && gtkSupported) detachKnTarget(this)
        }
    }
}