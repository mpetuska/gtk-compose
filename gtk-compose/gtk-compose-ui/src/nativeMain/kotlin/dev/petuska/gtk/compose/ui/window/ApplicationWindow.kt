package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Widget
import org.gtkkn.bindings.gtk.Window

@GtkComposeInternalApi
public open class ApplicationWindowNode(
    override val widget: ApplicationWindow
) : WindowNode<ApplicationWindow>(widget) {

    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }
}

@Composable
public fun ApplicationWindow(
    visible: Boolean,
    title: String? = null,
    child: ContentBuilder<Window>
) {
    Window(
        visible = visible,
        title = title,
        create = { application ->
            ApplicationWindowNode(ApplicationWindow(application))
        },
        update = {},
        child = child,
    )
}
