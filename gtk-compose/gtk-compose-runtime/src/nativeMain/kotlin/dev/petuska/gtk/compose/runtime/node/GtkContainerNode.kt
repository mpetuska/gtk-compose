package dev.petuska.gtk.compose.runtime.node

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkContainerNode = GtkContainerNode<Widget>

/**
 * A [GtkNode] that can have other [GtkNode]s as children
 */
@GtkComposeInternalApi
public abstract class GtkContainerNode<out TWidget : Widget> : GtkNode<TWidget>() {
    public abstract fun insert(index: Int, instance: AnyGtkNode)

    public abstract fun remove(index: Int, count: Int)

    public abstract fun move(from: Int, to: Int, count: Int)

    public abstract fun clear()
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkContainerNode<TWidget>> GtkContainerNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    crossinline content: ContentBuilder<TWidget>,
    noinline factory: () -> TNode,
) {
    GtkNode(
        factory = factory,
        update = update,
        content = content,
    )
}
