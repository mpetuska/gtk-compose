package dev.petuska.gtk.compose.samples.todo.view.task

import dev.petuska.gtk.compose.samples.todo.util.margin
import org.gtkkn.bindings.gtk.Box
import org.gtkkn.bindings.gtk.CheckButton
import org.gtkkn.bindings.gtk.Label
import org.gtkkn.bindings.gtk.Orientation

fun TodoTaskRow() = Box(Orientation.HORIZONTAL, 12).apply {
    CheckButton().apply {
        margin(12)
    }.let(::append)
    Label(null).apply {
        margin(12)
    }.let(::append)
}