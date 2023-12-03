package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkParentNode
import org.gtkkn.bindings.gtk.Button
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
private class ButtonNode(override val widget: Button) : GtkParentNode<Button>() {
    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }
}

@Composable
public fun Button(
//    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onClick: () -> Unit = {},
    child: ContentBuilder<Button>
) {
    @OptIn(GtkComposeInternalApi::class)
    GtkParentNode(
        update = {
            set(onClick) { println("Connecting clicked"); widget.connectClicked(handler = onClick) }
        },
        child = child
    ) {
        ButtonNode(Button())
    }
}
