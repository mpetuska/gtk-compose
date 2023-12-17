package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.GtkElementNode
import dev.petuska.gtk.compose.ui.node.GtkNode
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import dev.petuska.gtk.compose.ui.props.getValue
import dev.petuska.gtk.compose.ui.props.setValue
import org.gtkkn.bindings.gtk.Label

@GtkComposeInternalApi
private class LabelNode(override val widget: Label) : GtkElementNode<Label>() {
}

// region properties
/**
 * @see Label.label
 */
public var PropsScope<out Label>.label: String by Label::label
// endregion

@Composable
public fun Label(
    props: PropsBuilder<Label>,
) {
    GtkNode(
        props = props
    ) { LabelNode(Label(null)) }
}

@Composable
public fun Label(
    text: String,
) {
    Label(
        props = {
            label = text
        }
    )
}