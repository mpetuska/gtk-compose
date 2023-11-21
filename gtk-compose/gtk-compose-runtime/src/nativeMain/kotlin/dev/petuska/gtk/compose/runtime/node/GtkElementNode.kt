package dev.petuska.gtk.compose.runtime.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Updater
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkElementNode = GtkElementNode<Widget>

@GtkComposeInternalApi
public abstract class GtkElementNode<out TWidget : Widget> : GtkNode<TWidget>()

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkElementNode<TWidget>> GtkElementNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    noinline factory: () -> TNode
) {
    GtkNode(
        factory = factory,
        update = update,
    )
}