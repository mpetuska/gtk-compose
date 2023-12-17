package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import dev.petuska.gtk.compose.ui.props.getValue
import dev.petuska.gtk.compose.ui.props.setValue
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Window

@GtkComposeInternalApi
private class ApplicationWindowNode(
    override val widget: ApplicationWindow
) : WindowNode<ApplicationWindow>(widget)

// region properties
public var PropsScope<out ApplicationWindow>.showMenubar: Boolean by ApplicationWindow::showMenubar
// endregion

@Composable
public fun ApplicationWindow(
    onCloseRequest: () -> Unit,
    visible: Boolean,
    props: PropsScope<ApplicationWindow>.() -> Unit,
    child: ContentBuilder<Window>
) {
    Window(
        onCloseRequest = onCloseRequest,
        visible = visible,
        create = { application ->
            ApplicationWindowNode(ApplicationWindow(application))
        },
        dispose = {},
        props = props,
        child = child,
    )
}
