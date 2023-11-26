package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkParentNode
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
private class ApplicationWindowNode(override val widget: ApplicationWindow) : GtkParentNode<ApplicationWindow>() {

    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }
}

@Composable
public fun ApplicationWindow(
    application: Application,
    child: ContentBuilder<ApplicationWindow>
) {
    @OptIn(GtkComposeInternalApi::class)
    GtkParentNode(update = {}, child = {
        DisposableEffect(application) {
            scopeElement.present()
            onDispose { scopeElement.close() }
        }
        child()
    }) {
        val widget = ApplicationWindow(application)
        widget.present()
        ApplicationWindowNode(widget)
    }
}

/**
 * Use this method to mount the composition on the [ApplicationWindow].
 *
 * @param content - the Composable lambda that defines the composition content
 *
 * @return the instance of the [Composition]
 */
public fun <TWindow : ApplicationWindow> renderComposable(
    window: TWindow,
    child: ContentBuilder<ApplicationWindow>
): Composition {
    @OptIn(GtkComposeInternalApi::class)
    return dev.petuska.gtk.compose.ui.renderComposable(
        root = ApplicationWindowNode(window),
        content = {
            DisposableEffect(window) {
                scopeElement.present()
                onDispose { scopeElement.close() }
            }
            child()
        }
    )
}