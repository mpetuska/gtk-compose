@file:Suppress("unused")

package dev.petuska.gtk.compose.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gtkkn.gradle.plugin.GtkPlugin

class GtkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(GtkPlugin::class.java)
    }
}
