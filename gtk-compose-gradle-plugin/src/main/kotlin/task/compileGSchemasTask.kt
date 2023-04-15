package dev.petuska.gtk.compose.gradle.plugin.task

import dev.petuska.gtk.compose.gradle.plugin.ext.gtk
import dev.petuska.gtk.compose.gradle.plugin.utils.capitalise
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractExecutable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation

internal inline val KotlinNativeCompilation.compileGSchemasTaskName
    get() = "${target.name}${name.capitalise()}CompileGSchemas"

internal fun Project.compileGSchemasTask(
    compilation: KotlinNativeCompilation
) = tasks.register(compilation.compileGSchemasTaskName, Exec::class.java) {
    val copySchemasTask = tasks.named(compilation.copyGSchemasTaskName)
    dependsOn(copySchemasTask)
    outputs.upToDateWhen {
        !copySchemasTask.get().didWork
    }
    executable = "glib-compile-schemas"
    args(gtk.gSchemasDirectory.asFile.get().path)
}.also {
    compilation.target.binaries.whenObjectAdded {
        if (this is AbstractExecutable) tasks.named(linkTaskName) { dependsOn(it) }
    }
    compilation.target.binaries.whenObjectRemoved {
        if (this is AbstractExecutable) tasks.named(linkTaskName) { dependsOn.remove(it) }
    }
}
