package dev.petuska.gtk.compose.ui.node

import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkElementNode = GtkElementNode<Widget>

@GtkComposeInternalApi
public abstract class GtkElementNode<out TWidget : Widget> : GtkNode<TWidget>()
