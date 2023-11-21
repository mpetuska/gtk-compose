package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.runtime.node.GtkElementNode
import org.gtkkn.bindings.gtk.Label

@GtkComposeInternalApi
private class LabelNode(override val widget: Label) : GtkElementNode<Label>() {
}

@Composable
public fun Label(
//    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    text: String
) {
    @OptIn(GtkComposeInternalApi::class)
    GtkElementNode(
        update = {
            set(text) { widget.setText(it) }
        }
    ) { LabelNode(Label(text)) }
}