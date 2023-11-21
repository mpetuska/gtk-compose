package dev.petuska.gtk.compose.gradle.plugin.ext

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

typealias GtkExt = org.gtkkn.gradle.plugin.ext.GtkExt

internal inline val Project.gtk: GtkExt
    get() = extensions.getByType()
