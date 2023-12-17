package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SkippableUpdater
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkElementNode = GtkElementNode<Widget>

@GtkComposeInternalApi
public abstract class GtkElementNode<out TWidget : Widget> : GtkNode<TWidget>()
