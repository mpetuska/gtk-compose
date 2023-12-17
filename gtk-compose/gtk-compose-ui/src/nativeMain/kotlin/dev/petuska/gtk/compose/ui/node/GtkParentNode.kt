package dev.petuska.gtk.compose.ui.node

import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkParentNode = GtkParentNode<Widget>

/**
 * A specialised [GtkContainerNode] that only has a single child
 */
@GtkComposeInternalApi
public abstract class GtkParentNode<out TWidget : Widget> : GtkContainerNode<TWidget>() {
    private var child: AnyGtkNode? = null

    public abstract fun add(child: Widget)

    public final override fun insert(index: Int, instance: AnyGtkNode) {
        if (child != null) {
            clear()
        }
        add(instance.widget)
        child = instance
    }

    public final override fun remove(index: Int, count: Int) {
        if (index == 0 && child != null) {
            clear()
            child = null
        }
    }

    public final override fun move(from: Int, to: Int, count: Int) {
        // noop
    }
}
