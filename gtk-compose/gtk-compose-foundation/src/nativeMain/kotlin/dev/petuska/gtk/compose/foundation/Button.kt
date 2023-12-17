package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkNode
import dev.petuska.gtk.compose.ui.node.GtkParentNode
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import dev.petuska.gtk.compose.ui.props.signal
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

// region signals
/**
 * @see Button.connectClicked
 */
public var PropsScope<out Button>.onClick: () -> Unit by signal { widget.connectClicked(handler = it) }

/**
 * @see Button.connectActivate
 */
public var PropsScope<out Button>.onActivate: () -> Unit by signal { widget.connectActivate(handler = it) }
// endregion

@Composable
public fun Button(
    props: PropsBuilder<Button>,
    child: ContentBuilder<Button>
) {
    GtkNode(
        props = props,
        content = child
    ) {
        ButtonNode(Button())
    }
}
