package dev.petuska.gtk.compose.runtime.content

import org.gtkkn.bindings.gtk.Widget

public fun interface WidgetBuilder<TWidget : Widget> {
    public fun create(): TWidget
}