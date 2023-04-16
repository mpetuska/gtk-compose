package dev.petuska.gtk.compose.samples.todo.view.task

import org.gtkkn.bindings.gobject.Object
import org.gtkkn.native.gobject.g_object_new

data class TaskData(
    val completed:Boolean,
    val content: String,
)
