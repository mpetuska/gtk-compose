package dev.petuska.gtk.compose.gradle.plugin.task

import dev.petuska.gtk.compose.gradle.plugin.ext.gtk
import dev.petuska.gtk.compose.gradle.plugin.utils.capitalise
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation

internal inline val KotlinNativeCompilation.copyGSchemasTaskName
    get() = "${target.name}${name.capitalise()}CopyGSchemas"

internal fun Project.copyGSchemasTask(
    compilation: KotlinNativeCompilation
) = tasks.register(compilation.copyGSchemasTaskName, Copy::class.java) {
    val processResourcesTask = tasks.named(compilation.processResourcesTaskName)
    dependsOn(processResourcesTask)
    from(processResourcesTask) {
        include("**/*.gschema.xml")
    }
    into(gtk.gSchemasDirectory)
}