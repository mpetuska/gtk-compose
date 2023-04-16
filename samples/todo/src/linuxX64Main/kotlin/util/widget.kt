package dev.petuska.gtk.compose.samples.todo.util

import org.gtkkn.bindings.gtk.Align
import org.gtkkn.bindings.gtk.Widget


fun Widget.margin(margin: Int) {
    marginBottom = margin
    marginTop = margin
    marginStart = margin
    marginEnd = margin
}

fun Widget.align(align: Align) {
    valign = align
    halign = align
}
